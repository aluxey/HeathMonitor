package com.cyberdoc.app.domain.repository

import com.cyberdoc.app.domain.model.MetricRecord
import com.cyberdoc.app.domain.model.MetricType
import java.time.LocalDate

interface MetricRepository {
    suspend fun add(record: MetricRecord)
    suspend fun upsertAll(records: List<MetricRecord>)
    suspend fun findByDay(day: LocalDate): List<MetricRecord>
    suspend fun latest(metricType: MetricType): MetricRecord?
}
