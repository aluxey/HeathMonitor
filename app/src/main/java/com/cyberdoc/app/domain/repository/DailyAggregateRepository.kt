package com.cyberdoc.app.domain.repository

import com.cyberdoc.app.domain.model.DailyAggregate
import com.cyberdoc.app.domain.model.MetricType
import java.time.LocalDate

interface DailyAggregateRepository {
    suspend fun upsertAll(aggregates: List<DailyAggregate>)
    suspend fun byDate(date: LocalDate): List<DailyAggregate>
    suspend fun trend(metricType: MetricType, from: LocalDate, to: LocalDate): List<DailyAggregate>
}
