package com.cyberdoc.app.data.repository.room

import com.cyberdoc.app.data.local.dao.DailyAggregateDao
import com.cyberdoc.app.domain.model.DailyAggregate
import com.cyberdoc.app.domain.model.MetricType
import com.cyberdoc.app.domain.repository.DailyAggregateRepository
import java.time.LocalDate

class RoomDailyAggregateRepository(
    private val dao: DailyAggregateDao,
) : DailyAggregateRepository {
    override suspend fun upsertAll(aggregates: List<DailyAggregate>) {
        if (aggregates.isEmpty()) return
        dao.upsertAll(aggregates.map { it.toEntity() })
    }

    override suspend fun byDate(date: LocalDate): List<DailyAggregate> =
        dao.byDate(date.toString()).map { it.toDomain() }

    override suspend fun trend(metricType: MetricType, from: LocalDate, to: LocalDate): List<DailyAggregate> =
        dao.trend(metricType = metricType.name, fromDate = from.toString(), toDate = to.toString())
            .map { it.toDomain() }
}
