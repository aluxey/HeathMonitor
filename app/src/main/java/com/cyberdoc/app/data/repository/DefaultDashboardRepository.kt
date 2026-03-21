package com.cyberdoc.app.data.repository

import com.cyberdoc.app.data.local.dao.DailyAggregateDao
import com.cyberdoc.app.data.local.dao.DataSourceDao
import com.cyberdoc.app.data.local.dao.GoalDao
import com.cyberdoc.app.data.local.entity.DailyAggregateEntity
import com.cyberdoc.app.data.local.entity.GoalEntity
import com.cyberdoc.app.domain.model.DashboardMetric
import com.cyberdoc.app.domain.model.MetricType
import com.cyberdoc.app.domain.model.SourceType
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
    override fun observeToday(date: LocalDate): Flow<List<DashboardMetric>> =
        combine(
            aggregateDao.observeByDate(date.toString()),
            goalDao.observeActiveGoals(),
            sourceDao.observeAll(),
        ) { aggregates, goals, sources ->
            MetricType.entries.map { metricType ->
                val aggregate = aggregates.firstOrNull { it.metricType == metricType }
                val goal = goals.firstOrNull { it.metricType == metricType }
                val sourceName = aggregate?.sourceId?.let { sourceId ->
                    sources.firstOrNull { it.id == sourceId }?.displayName
                } ?: "Saisie manuelle"

                buildMetricCard(
                    metricType = metricType,
                    aggregate = aggregate,
                    goal = goal,
                    sourceName = sourceName,
                )
            }
        }

    private fun buildMetricCard(
        metricType: MetricType,
        aggregate: DailyAggregateEntity?,
        goal: GoalEntity?,
        sourceName: String,
    ): DashboardMetric {
        val target = goal?.targetValue ?: 0.0
        val current = aggregate?.value ?: 0.0
        val progress = if (target > 0.0) (current / target).toFloat().coerceIn(0f, 1f) else 0f
        val freshness = aggregate?.computedAt?.let(::formatFreshness) ?: "Pas encore synchronise"

        return DashboardMetric(
            metricType = metricType,
            title = metricType.title(),
            valueLabel = metricType.formatValue(current),
            targetLabel = if (target > 0.0) "Objectif ${metricType.formatValue(target)}" else "Objectif non defini",
            freshnessLabel = freshness,
            sourceLabel = sourceName,
            progress = progress,
            trendLabel = if (aggregate == null) "Aucune donnee" else "Base jour actif pour futures tendances",
            qualityFlag = aggregate?.qualityFlag ?: com.cyberdoc.app.domain.model.QualityFlag.MISSING,
        )
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

    private fun formatFreshness(timestamp: Long): String {
        val dateTime = Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
        return "Maj ${dateTime.format(DateTimeFormatter.ofPattern("dd/MM HH:mm"))}"
    }
}
