package com.cyberdoc.app.data.repository.room

import com.cyberdoc.app.core.TimeProvider
import com.cyberdoc.app.core.dayRange
import com.cyberdoc.app.core.metricLocalDate
import com.cyberdoc.app.data.local.dao.DataSourceDao
import com.cyberdoc.app.data.local.dao.GoalDao
import com.cyberdoc.app.data.local.dao.MetricRecordDao
import com.cyberdoc.app.data.local.entity.MetricRecordEntity
import com.cyberdoc.app.domain.model.DashboardMetric
import com.cyberdoc.app.domain.model.DashboardSnapshot
import com.cyberdoc.app.domain.model.MetricType
import com.cyberdoc.app.domain.repository.DashboardRepository
import com.cyberdoc.app.domain.repository.MetricSourceSettingsRepository
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.abs

class RoomDashboardRepository(
    private val metricDao: MetricRecordDao,
    private val goalDao: GoalDao,
    private val sourceDao: DataSourceDao,
    private val metricSourceSettingsRepository: MetricSourceSettingsRepository,
    private val timeProvider: TimeProvider,
) : DashboardRepository {
    private val zoneId: ZoneId = ZoneId.systemDefault()
    private val dailyMetricTypes = setOf(
        MetricType.STEPS,
        MetricType.EXERCISE_DURATION,
        MetricType.HYDRATION,
        MetricType.CALORIES_IN,
    )

    override suspend fun snapshot(): DashboardSnapshot {
        val today = timeProvider.now().atZone(zoneId).toLocalDate()
        val metrics = MetricType.entries.mapNotNull { metricType ->
            val sourceSetting = metricSourceSettingsRepository.setting(metricType)
            val effectiveSourceId = sourceSetting.effectiveSourceId
            val latest = effectiveSourceId?.let { metricDao.latestBySource(metricType.name, it) }
                ?: metricDao.latest(metricType.name)
                ?: return@mapNotNull null

            val goal = goalDao.activeGoal(metricType.name)
            val anchorDate = if (metricType in dailyMetricTypes) {
                today
            } else {
                metricLocalDate(
                    metricType = metricType,
                    startAt = Instant.ofEpochMilli(latest.startAtEpochMillis),
                    endAt = Instant.ofEpochMilli(latest.endAtEpochMillis),
                )
            }

            val monthValues = denseSeries(
                metricType = metricType,
                sourceId = effectiveSourceId,
                fromDate = anchorDate.minusDays(29),
                toDate = anchorDate,
            )
            val weekValues = monthValues.takeLast(7)

            if (metricType in dailyMetricTypes && (weekValues.lastOrNull() ?: 0.0) == 0.0) {
                return@mapNotNull null
            }

            DashboardMetric(
                metricType = metricType,
                value = monthValues.lastOrNull() ?: aggregateValue(metricType, listOf(latest)),
                unit = latest.unit,
                trendPercent = calculateTrendPercent(weekValues),
                goalTarget = goal?.targetValue,
                sourceId = effectiveSourceId ?: latest.sourceId,
                weekValues = weekValues,
                monthValues = monthValues,
            )
        }

        return DashboardSnapshot(
            generatedAt = timeProvider.now(),
            metrics = metrics,
            sources = sourceDao.all().map { it.toDomain() },
        )
    }

    private suspend fun denseSeries(
        metricType: MetricType,
        sourceId: String?,
        fromDate: LocalDate,
        toDate: LocalDate,
    ): List<Double> {
        val fromEpochMillis = dayRange(fromDate).first
        val toEpochMillis = dayRange(toDate).second
        val records = sourceId?.let { selectedSourceId ->
            metricDao.byMetricAndSource(
                metricType = metricType.name,
                sourceId = selectedSourceId,
                fromEpochMillis = fromEpochMillis,
                toEpochMillis = toEpochMillis,
            )
        } ?: metricDao.byMetric(
            metricType = metricType.name,
            fromEpochMillis = fromEpochMillis,
            toEpochMillis = toEpochMillis,
        )

        val valuesByDate = records
            .groupBy {
                metricLocalDate(
                    metricType = metricType,
                    startAt = Instant.ofEpochMilli(it.startAtEpochMillis),
                    endAt = Instant.ofEpochMilli(it.endAtEpochMillis),
                )
            }
            .mapValues { (_, grouped) -> aggregateValue(metricType, grouped) }

        return generateSequence(fromDate) { current ->
            current.plusDays(1).takeIf { it <= toDate }
        }
            .map { date -> valuesByDate[date] ?: 0.0 }
            .toList()
    }

    private fun aggregateValue(
        metricType: MetricType,
        records: List<MetricRecordEntity>,
    ): Double {
        val sample = records.maxByOrNull { it.endAtEpochMillis } ?: return 0.0
        return when (metricType) {
            MetricType.STEPS,
            MetricType.SLEEP_DURATION,
            MetricType.HYDRATION,
            MetricType.CALORIES_IN,
            MetricType.EXERCISE_DURATION -> records.sumOf { it.value }

            MetricType.WEIGHT,
            MetricType.HEART_RATE -> sample.value
        }
    }

    private fun calculateTrendPercent(values: List<Double>): Double {
        if (values.size < 2) return 0.0

        val latest = values.last()
        val previous = values.dropLast(1).lastOrNull { it > 0.0 }
            ?: values.dropLast(1).lastOrNull()
            ?: return 0.0

        if (previous == 0.0) {
            return if (latest > 0.0) 100.0 else 0.0
        }

        return ((latest - previous) / abs(previous)) * 100.0
    }
}
