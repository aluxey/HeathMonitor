package com.cyberdoc.app.domain.usecase

import com.cyberdoc.app.core.AppResult
import com.cyberdoc.app.core.TimeProvider
import com.cyberdoc.app.core.ValidationError
import com.cyberdoc.app.domain.model.DataSource
import com.cyberdoc.app.domain.model.MetricRecord
import com.cyberdoc.app.domain.model.SourceStatus
import com.cyberdoc.app.domain.model.SourceType
import com.cyberdoc.app.domain.repository.DailyAggregateRepository
import com.cyberdoc.app.domain.repository.MetricRepository
import com.cyberdoc.app.domain.repository.SourceRepository
import com.cyberdoc.app.domain.service.DailyAggregateCalculator
import java.time.ZoneOffset

class RegisterManualMetricUseCase(
    private val metricRepository: MetricRepository,
    private val dailyAggregateRepository: DailyAggregateRepository,
    private val sourceRepository: SourceRepository,
    private val aggregateCalculator: DailyAggregateCalculator,
    private val timeProvider: TimeProvider,
) {
    suspend operator fun invoke(record: MetricRecord): AppResult<Unit> {
        if (record.value <= 0.0) {
            return AppResult.Failure(ValidationError("Metric value must be greater than zero"))
        }

        val recordedAt = timeProvider.now()
        if (sourceRepository.byId(MANUAL_SOURCE_ID) == null) {
            sourceRepository.upsert(
                DataSource(
                    id = MANUAL_SOURCE_ID,
                    type = SourceType.MANUAL,
                    displayName = "Manual entry",
                    status = SourceStatus.CONNECTED,
                    priority = 1,
                    lastSyncAt = recordedAt,
                    lastError = null,
                ),
            )
        }

        val manualRecord = record.copy(
            sourceId = MANUAL_SOURCE_ID,
            isManual = true,
            createdAt = recordedAt,
        )
        metricRepository.add(manualRecord)

        val day = manualRecord.startAt.atZone(ZoneOffset.UTC).toLocalDate()
        val dayRecords = metricRepository.findByDay(day)
        val aggregates = aggregateCalculator.calculate(
            records = dayRecords,
            computedAt = recordedAt,
        )
        dailyAggregateRepository.upsertAll(aggregates)
        return AppResult.Success(Unit)
    }

    companion object {
        const val MANUAL_SOURCE_ID = "manual"
    }
}
