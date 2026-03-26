package com.cyberdoc.app.data.repository.room

import com.cyberdoc.app.core.dayRange
import com.cyberdoc.app.core.metricLocalDate
import com.cyberdoc.app.data.local.dao.MetricRecordDao
import com.cyberdoc.app.domain.model.MetricRecord
import com.cyberdoc.app.domain.model.MetricType
import com.cyberdoc.app.domain.repository.MetricRepository
import java.time.Instant
import java.time.LocalDate

class RoomMetricRepository(
    private val dao: MetricRecordDao,
) : MetricRepository {
    override suspend fun add(record: MetricRecord) {
        dao.upsert(record.toEntity())
    }

    override suspend fun upsertAll(records: List<MetricRecord>) {
        if (records.isEmpty()) return
        dao.upsertAll(records.map { it.toEntity() })
    }

    override suspend fun deleteImportedInRange(from: Instant, to: Instant) {
        dao.deleteImportedInRange(
            fromEpochMillis = from.toEpochMilli(),
            toEpochMillis = to.toEpochMilli(),
        )
    }

    override suspend fun findByDay(day: LocalDate): List<MetricRecord> {
        val (from, to) = dayRange(day)
        return dao.byDay(fromEpochMillis = from, toEpochMillis = to)
            .map { it.toDomain() }
            .filter { record ->
                metricLocalDate(
                    metricType = record.metricType,
                    startAt = record.startAt,
                    endAt = record.endAt,
                ) == day
            }
    }

    override suspend fun latest(metricType: MetricType): MetricRecord? =
        dao.latest(metricType.name)?.toDomain()
}
