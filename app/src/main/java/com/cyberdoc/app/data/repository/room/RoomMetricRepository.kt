package com.cyberdoc.app.data.repository.room

import com.cyberdoc.app.data.local.dao.MetricRecordDao
import com.cyberdoc.app.domain.model.MetricRecord
import com.cyberdoc.app.domain.model.MetricType
import com.cyberdoc.app.domain.repository.MetricRepository
import java.time.LocalDate
import java.time.ZoneOffset

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

    override suspend fun findByDay(day: LocalDate): List<MetricRecord> {
        val from = day.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        val to = day.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        return dao.byDay(fromEpochMillis = from, toEpochMillis = to).map { it.toDomain() }
    }

    override suspend fun latest(metricType: MetricType): MetricRecord? =
        dao.latest(metricType.name)?.toDomain()
}
