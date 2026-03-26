package com.cyberdoc.app.domain.service

import com.cyberdoc.app.core.metricLocalDate
import com.cyberdoc.app.domain.model.DailyAggregate
import com.cyberdoc.app.domain.model.MetricRecord
import com.cyberdoc.app.domain.model.MetricType
import com.cyberdoc.app.domain.model.QualityFlag
import java.time.Instant
import java.util.UUID

class DailyAggregateCalculator {
    fun calculate(records: List<MetricRecord>, computedAt: Instant): List<DailyAggregate> =
        records
            .groupBy { record ->
                Pair(
                    metricLocalDate(
                        metricType = record.metricType,
                        startAt = record.startAt,
                        endAt = record.endAt,
                    ),
                    record.metricType,
                )
            }
            .map { (key, grouped) ->
                val date = key.first
                val metricType = key.second
                val chosenValue = when (metricType) {
                    MetricType.STEPS,
                    MetricType.SLEEP_DURATION,
                    MetricType.HYDRATION,
                    MetricType.CALORIES_IN,
                    MetricType.EXERCISE_DURATION -> grouped.sumOf { it.value }

                    MetricType.WEIGHT,
                    MetricType.HEART_RATE -> grouped.maxByOrNull { it.endAt }?.value ?: 0.0
                }
                val sample = grouped.maxByOrNull { it.endAt } ?: grouped.first()
                DailyAggregate(
                    id = UUID.randomUUID().toString(),
                    date = date,
                    metricType = metricType,
                    value = chosenValue,
                    unit = sample.unit,
                    sourceId = sample.sourceId,
                    qualityFlag = QualityFlag.OK,
                    computedAt = computedAt,
                )
            }
}
