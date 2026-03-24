package com.cyberdoc.app.data.repository.room

import com.cyberdoc.app.core.TimeProvider
import com.cyberdoc.app.data.local.dao.DailyAggregateDao
import com.cyberdoc.app.data.local.dao.DataSourceDao
import com.cyberdoc.app.data.local.dao.GoalDao
import com.cyberdoc.app.data.local.dao.MetricRecordDao
import com.cyberdoc.app.domain.model.DashboardMetric
import com.cyberdoc.app.domain.model.DashboardSnapshot
import com.cyberdoc.app.domain.model.MetricType
import com.cyberdoc.app.domain.repository.DashboardRepository
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.abs

class RoomDashboardRepository(
    private val aggregateDao: DailyAggregateDao,
    private val metricDao: MetricRecordDao,
    private val goalDao: GoalDao,
    private val sourceDao: DataSourceDao,
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
        val todayAggregates = aggregateDao.byDate(today.toString()).associateBy { it.metricType }
        val metrics = MetricType.entries.mapNotNull { metricType ->
            val latestAggregate = aggregateDao.latest(metricType.name)
            val latest = metricDao.latest(metricType.name) ?: return@mapNotNull null
            val goal = goalDao.activeGoal(metricType.name)
            val displayAggregate = if (metricType in dailyMetricTypes) {
                todayAggregates[metricType.name]
            } else {
                latestAggregate
            }

            if (metricType in dailyMetricTypes && displayAggregate == null) {
                return@mapNotNull null
            }

            val anchorDate = displayAggregate?.let { LocalDate.parse(it.date) }
                ?: latestAggregate?.let { LocalDate.parse(it.date) }
                ?: Instant.ofEpochMilli(latest.endAtEpochMillis).atZone(zoneId).toLocalDate()
            val weekValues = (displayAggregate ?: latestAggregate)?.let {
                denseSeries(
                    metricType = metricType,
                    fromDate = anchorDate.minusDays(6),
                    toDate = anchorDate,
                )
            } ?: fallbackSeries(days = 7, latestValue = latest.value)
            val monthValues = (displayAggregate ?: latestAggregate)?.let {
                denseSeries(
                    metricType = metricType,
                    fromDate = anchorDate.minusDays(29),
                    toDate = anchorDate,
                )
            } ?: fallbackSeries(days = 30, latestValue = latest.value)
            DashboardMetric(
                metricType = metricType,
                value = displayAggregate?.value ?: latestAggregate?.value ?: latest.value,
                unit = displayAggregate?.unit ?: latestAggregate?.unit ?: latest.unit,
                trendPercent = calculateTrendPercent(weekValues),
                goalTarget = goal?.targetValue,
                sourceId = displayAggregate?.sourceId ?: latestAggregate?.sourceId ?: latest.sourceId,
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
        fromDate: LocalDate,
        toDate: LocalDate,
    ): List<Double> {
        val byDate = aggregateDao.trend(
            metricType = metricType.name,
            fromDate = fromDate.toString(),
            toDate = toDate.toString(),
        ).associateBy { LocalDate.parse(it.date) }

        return generateSequence(fromDate) { current ->
            current.plusDays(1).takeIf { it <= toDate }
        }
            .map { date -> byDate[date]?.value ?: 0.0 }
            .toList()
    }

    private fun fallbackSeries(days: Int, latestValue: Double): List<Double> =
        List(days - 1) { 0.0 } + latestValue

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
