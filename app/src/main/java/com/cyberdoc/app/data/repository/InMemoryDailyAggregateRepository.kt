package com.cyberdoc.app.data.repository

import com.cyberdoc.app.data.inmemory.InMemoryStore
import com.cyberdoc.app.domain.model.DailyAggregate
import com.cyberdoc.app.domain.model.MetricType
import com.cyberdoc.app.domain.repository.DailyAggregateRepository
import java.time.LocalDate

class InMemoryDailyAggregateRepository(
    private val store: InMemoryStore,
) : DailyAggregateRepository {
    override suspend fun upsertAll(aggregates: List<DailyAggregate>) {
        aggregates.forEach { incoming ->
            val index = store.dailyAggregates.indexOfFirst {
                it.date == incoming.date &&
                    it.metricType == incoming.metricType &&
                    it.sourceId == incoming.sourceId
            }
            if (index == -1) {
                store.dailyAggregates += incoming
            } else {
                store.dailyAggregates[index] = incoming
            }
        }
    }

    override suspend fun deleteByDateRange(from: LocalDate, to: LocalDate) {
        store.dailyAggregates.removeAll { aggregate ->
            !aggregate.date.isBefore(from) && !aggregate.date.isAfter(to)
        }
    }

    override suspend fun byDate(date: LocalDate): List<DailyAggregate> =
        store.dailyAggregates
            .asSequence()
            .filter { it.date == date }
            .sortedBy { it.metricType.name }
            .toList()

    override suspend fun trend(metricType: MetricType, from: LocalDate, to: LocalDate): List<DailyAggregate> =
        store.dailyAggregates
            .asSequence()
            .filter {
                it.metricType == metricType &&
                    !it.date.isBefore(from) &&
                    !it.date.isAfter(to)
            }
            .sortedBy { it.date }
            .toList()
}
