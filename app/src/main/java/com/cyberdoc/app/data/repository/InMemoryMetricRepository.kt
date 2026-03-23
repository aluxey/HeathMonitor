package com.cyberdoc.app.data.repository

import com.cyberdoc.app.data.inmemory.InMemoryStore
import com.cyberdoc.app.domain.model.MetricRecord
import com.cyberdoc.app.domain.model.MetricType
import com.cyberdoc.app.domain.repository.MetricRepository
import java.time.LocalDate
import java.time.ZoneOffset

class InMemoryMetricRepository(
    private val store: InMemoryStore,
) : MetricRepository {
    override suspend fun add(record: MetricRecord) {
        store.metrics += record
    }

    override suspend fun findByDay(day: LocalDate): List<MetricRecord> =
        store.metrics.filter {
            it.startAt.atZone(ZoneOffset.UTC).toLocalDate() == day
        }

    override suspend fun latest(metricType: MetricType): MetricRecord? =
        store.metrics
            .asSequence()
            .filter { it.metricType == metricType }
            .maxByOrNull { it.endAt }
}
