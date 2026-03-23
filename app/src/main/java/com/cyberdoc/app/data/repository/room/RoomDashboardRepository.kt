package com.cyberdoc.app.data.repository.room

import com.cyberdoc.app.core.TimeProvider
import com.cyberdoc.app.data.local.dao.DataSourceDao
import com.cyberdoc.app.data.local.dao.GoalDao
import com.cyberdoc.app.data.local.dao.MetricRecordDao
import com.cyberdoc.app.domain.model.DashboardMetric
import com.cyberdoc.app.domain.model.DashboardSnapshot
import com.cyberdoc.app.domain.model.MetricType
import com.cyberdoc.app.domain.repository.DashboardRepository

class RoomDashboardRepository(
    private val metricDao: MetricRecordDao,
    private val goalDao: GoalDao,
    private val sourceDao: DataSourceDao,
    private val timeProvider: TimeProvider,
) : DashboardRepository {
    override suspend fun snapshot(): DashboardSnapshot {
        val metrics = MetricType.entries.mapNotNull { metricType ->
            val latest = metricDao.latest(metricType.name) ?: return@mapNotNull null
            val goal = goalDao.activeGoal(metricType.name)
            DashboardMetric(
                metricType = metricType,
                value = latest.value,
                unit = latest.unit,
                trendPercent = 0.0,
                goalTarget = goal?.targetValue,
            )
        }

        return DashboardSnapshot(
            generatedAt = timeProvider.now(),
            metrics = metrics,
            sources = sourceDao.all().map { it.toDomain() },
        )
    }
}
