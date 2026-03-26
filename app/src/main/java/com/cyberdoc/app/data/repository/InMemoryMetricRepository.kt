package com.cyberdoc.app.data.repository

import com.cyberdoc.app.core.metricLocalDate
import com.cyberdoc.app.data.inmemory.InMemoryStore
import com.cyberdoc.app.domain.model.MetricRecord
import com.cyberdoc.app.domain.model.MetricType
import com.cyberdoc.app.domain.repository.MetricRepository
import java.time.Instant
import java.time.LocalDate

class InMemoryMetricRepository(
    private val store: InMemoryStore,
) : MetricRepository {
    override suspend fun add(record: MetricRecord) {
        store.metrics += record
    }

    override suspend fun upsertAll(records: List<MetricRecord>) {
        records.forEach { incoming ->
            val index = store.metrics.indexOfFirst {
                it.metricType == incoming.metricType &&
                    it.startAt == incoming.startAt &&
                    it.endAt == incoming.endAt &&
                    it.sourceId == incoming.sourceId &&
                    it.externalId == incoming.externalId
            }
            if (index == -1) {
                store.metrics += incoming
            } else {
                store.metrics[index] = incoming
            }
        }
    }

    override suspend fun deleteImportedInRange(from: Instant, to: Instant) {
        store.metrics.removeAll { record ->
            !record.isManual &&
                record.endAt > from &&
                record.startAt < to
        }
    }

    override suspend fun findByDay(day: LocalDate): List<MetricRecord> =
        store.metrics.filter {
            metricLocalDate(
                metricType = it.metricType,
                startAt = it.startAt,
                endAt = it.endAt,
            ) == day
        }

    override suspend fun latest(metricType: MetricType): MetricRecord? =
        store.metrics
            .asSequence()
            .filter { it.metricType == metricType }
            .maxByOrNull { it.endAt }
}
