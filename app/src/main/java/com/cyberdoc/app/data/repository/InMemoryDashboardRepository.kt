package com.cyberdoc.app.data.repository

import com.cyberdoc.app.core.TimeProvider
import com.cyberdoc.app.data.inmemory.InMemoryStore
import com.cyberdoc.app.domain.model.DashboardMetric
import com.cyberdoc.app.domain.model.DashboardSnapshot
import com.cyberdoc.app.domain.model.MetricType
import com.cyberdoc.app.domain.repository.DashboardRepository

class InMemoryDashboardRepository(
    private val store: InMemoryStore,
    private val timeProvider: TimeProvider,
) : DashboardRepository {
    override suspend fun snapshot(): DashboardSnapshot {
        val metrics = MetricType.entries.mapNotNull { type ->
            val latest = store.metrics
                .asSequence()
                .filter { it.metricType == type }
                .maxByOrNull { it.endAt }
                ?: return@mapNotNull null

            val goal = store.goals.firstOrNull { it.metricType == type && it.isActive }
            DashboardMetric(
                metricType = latest.metricType,
                value = latest.value,
                unit = latest.unit,
                trendPercent = 0.0,
                goalTarget = goal?.targetValue,
                sourceId = latest.sourceId,
                weekValues = listOf(latest.value),
                monthValues = listOf(latest.value),
            )
        }

        return DashboardSnapshot(
            generatedAt = timeProvider.now(),
            metrics = metrics,
            sources = store.sources.sortedBy { it.priority },
        )
    }
}
