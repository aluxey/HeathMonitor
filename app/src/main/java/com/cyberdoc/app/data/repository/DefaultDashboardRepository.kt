package com.cyberdoc.app.data.repository

import com.cyberdoc.app.data.local.dao.DailyAggregateDao
import com.cyberdoc.app.data.local.dao.DataSourceDao
import com.cyberdoc.app.data.local.dao.GoalDao
import com.cyberdoc.app.data.local.entity.DailyAggregateEntity
import com.cyberdoc.app.data.local.entity.GoalEntity
import com.cyberdoc.app.domain.model.DashboardMetric
import com.cyberdoc.app.domain.model.MetricType
import com.cyberdoc.app.domain.model.TrendPoint
import com.cyberdoc.app.domain.repository.DashboardRepository
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class DefaultDashboardRepository(
    private val aggregateDao: DailyAggregateDao,
    private val goalDao: GoalDao,
    private val sourceDao: DataSourceDao,
) : DashboardRepository {
    override fun observeDashboard(date: LocalDate, periodDays: Int): Flow<List<DashboardMetric>> =
        combine(
            aggregateDao.observeBetween(
                startDate = date.minusDays((periodDays - 1).toLong()).toString(),
                endDate = date.toString(),
            ),
            goalDao.observeActiveGoals(),
            sourceDao.observeAll(),
        ) { aggregatesInRange, goals, sources ->
            MetricType.entries.map { metricType ->
                val aggregate = aggregatesInRange.firstOrNull {
                    it.metricType == metricType && it.date == date.toString()
                }
                val goal = goals.firstOrNull { it.metricType == metricType }
                val metricHistory = aggregatesInRange.filter { it.metricType == metricType }
                val sourceName = aggregate?.sourceId?.let { sourceId ->
                    sources.firstOrNull { it.id == sourceId }?.displayName
                } ?: "Saisie manuelle"

                buildMetricCard(
                    date = date,
                    periodDays = periodDays,
                    metricType = metricType,
                    aggregate = aggregate,
                    metricHistory = metricHistory,
                    goal = goal,
                    sourceName = sourceName,
                )
            }
        }

    private fun buildMetricCard(
        date: LocalDate,
        periodDays: Int,
        metricType: MetricType,
        aggregate: DailyAggregateEntity?,
        metricHistory: List<DailyAggregateEntity>,
        goal: GoalEntity?,
        sourceName: String,
    ): DashboardMetric {
        val target = goal?.targetValue ?: 0.0
        val current = aggregate?.value ?: 0.0
        val progress = if (target > 0.0) (current / target).toFloat().coerceIn(0f, 1f) else 0f
        val freshness = aggregate?.computedAt?.let(::formatFreshness) ?: "Pas encore synchronise"
        val trendPoints = buildTrendPoints(date, periodDays, metricHistory)
        val historyValues = trendPoints.mapNotNull { it.value }
        val average = historyValues.takeIf { it.isNotEmpty() }?.average()
        val missingDays = trendPoints.count { it.value == null }

        return DashboardMetric(
            metricType = metricType,
            title = metricType.title(),
            valueLabel = if (aggregate == null) "Aucune donnee" else metricType.formatValue(current),
            targetLabel = if (target > 0.0) "Objectif ${metricType.formatValue(target)}" else "Objectif non defini",
            deltaLabel = buildDeltaLabel(metricType, aggregate?.value, target, average),
            freshnessLabel = freshness,
            sourceLabel = sourceName,
            progress = progress,
            trendLabel = buildTrendLabel(metricType, periodDays, average, missingDays),
            trendPoints = trendPoints,
            qualityFlag = aggregate?.qualityFlag ?: com.cyberdoc.app.domain.model.QualityFlag.MISSING,
        )
    }

    private fun buildTrendPoints(
        date: LocalDate,
        periodDays: Int,
        metricHistory: List<DailyAggregateEntity>,
    ): List<TrendPoint> {
        val byDate = metricHistory.associateBy { it.date }
        return (periodDays - 1 downTo 0).map { offset ->
            val day = date.minusDays(offset.toLong())
            TrendPoint(
                dateLabel = day.format(DateTimeFormatter.ofPattern("dd/MM")),
                value = byDate[day.toString()]?.value,
            )
        }
    }

    private fun buildDeltaLabel(
        metricType: MetricType,
        current: Double?,
        target: Double,
        average: Double?,
    ): String {
        if (current == null) {
            return "Aucun relevé aujourd'hui"
        }
        if (target > 0.0) {
            val deltaToTarget = current - target
            val label = if (deltaToTarget >= 0.0) "au-dessus" else "en dessous"
            return "${metricType.formatSignedValue(kotlin.math.abs(deltaToTarget))} $label de l'objectif"
        }
        if (average != null) {
            val deltaToAverage = current - average
            val label = if (deltaToAverage >= 0.0) "vs moyenne" else "sous moyenne"
            return "${metricType.formatSignedValue(kotlin.math.abs(deltaToAverage))} $label"
        }
        return "Pas assez d'historique"
    }

    private fun buildTrendLabel(
        metricType: MetricType,
        periodDays: Int,
        average: Double?,
        missingDays: Int,
    ): String {
        val averageLabel = average?.let { "Moy $periodDays j ${metricType.formatValue(it)}" }
            ?: "Aucune tendance exploitable"
        return if (missingDays > 0) {
            "$averageLabel • $missingDays jour(s) manquant(s)"
        } else {
            averageLabel
        }
    }

    private fun MetricType.title(): String = when (this) {
        MetricType.STEPS -> "Pas"
        MetricType.SLEEP_DURATION -> "Sommeil"
        MetricType.WEIGHT -> "Poids"
        MetricType.CALORIES_IN -> "Calories"
        MetricType.EXERCISE_DURATION -> "Exercice"
    }

    private fun MetricType.formatValue(value: Double): String = when (this) {
        MetricType.STEPS -> "${value.toInt()} pas"
        MetricType.SLEEP_DURATION -> "${"%.1f".format(value)} h"
        MetricType.WEIGHT -> "${"%.1f".format(value)} kg"
        MetricType.CALORIES_IN -> "${value.toInt()} kcal"
        MetricType.EXERCISE_DURATION -> "${value.toInt()} min"
    }

    private fun MetricType.formatSignedValue(value: Double): String = when (this) {
        MetricType.STEPS -> "${value.toInt()} pas"
        MetricType.SLEEP_DURATION -> "${"%.1f".format(value)} h"
        MetricType.WEIGHT -> "${"%.1f".format(value)} kg"
        MetricType.CALORIES_IN -> "${value.toInt()} kcal"
        MetricType.EXERCISE_DURATION -> "${value.toInt()} min"
    }

    private fun formatFreshness(timestamp: Long): String {
        val dateTime = Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
        return "Maj ${dateTime.format(DateTimeFormatter.ofPattern("dd/MM HH:mm"))}"
    }
}
