package com.cyberdoc.app.domain.usecase

import com.cyberdoc.app.core.AppResult
import com.cyberdoc.app.core.IntegrationUnavailableError
import com.cyberdoc.app.core.TimeProvider
import com.cyberdoc.app.core.UnexpectedError
import com.cyberdoc.app.domain.model.DataSource
import com.cyberdoc.app.domain.model.SourceStatus
import com.cyberdoc.app.domain.model.SourceType
import com.cyberdoc.app.domain.model.SyncRun
import com.cyberdoc.app.domain.model.SyncStatus
import com.cyberdoc.app.domain.repository.DailyAggregateRepository
import com.cyberdoc.app.domain.repository.MetricRepository
import com.cyberdoc.app.domain.repository.SourceRepository
import com.cyberdoc.app.domain.repository.SyncRepository
import com.cyberdoc.app.domain.service.DailyAggregateCalculator
import com.cyberdoc.app.domain.service.MetricsNormalizer
import com.cyberdoc.app.integration.healthconnect.HealthConnectAvailability
import com.cyberdoc.app.integration.healthconnect.HealthConnectRepository
import java.time.temporal.ChronoUnit
import java.util.UUID

class SyncHealthConnectDataUseCase(
    private val healthConnectRepository: HealthConnectRepository,
    private val sourceRepository: SourceRepository,
    private val metricRepository: MetricRepository,
    private val dailyAggregateRepository: DailyAggregateRepository,
    private val syncRepository: SyncRepository,
    private val normalizer: MetricsNormalizer,
    private val aggregateCalculator: DailyAggregateCalculator,
    private val timeProvider: TimeProvider,
) {
    suspend operator fun invoke(daysBack: Long = 7): AppResult<SyncRun> {
        return try {
            val startedAt = timeProvider.now()
            ensureHealthConnectSource(startedAt)
            val availability = healthConnectRepository.availability()

            if (availability != HealthConnectAvailability.AVAILABLE) {
                sourceRepository.upsert(
                    healthConnectSource(
                        status = SourceStatus.ERROR,
                        lastSyncAt = startedAt,
                        lastError = "Health Connect unavailable: $availability",
                    ),
                )
                return AppResult.Failure(
                    IntegrationUnavailableError("Health Connect unavailable: $availability"),
                )
            }

            val from = startedAt.minus(daysBack, ChronoUnit.DAYS)
            val readResult = healthConnectRepository.readRecords(from = from, to = startedAt)
            val normalized = normalizer.normalize(
                records = readResult.records,
                importedAt = startedAt,
                sourceId = HEALTH_CONNECT_SOURCE_ID,
            )
            metricRepository.upsertAll(normalized)

            val aggregates = aggregateCalculator.calculate(
                records = normalized,
                computedAt = startedAt,
            )
            dailyAggregateRepository.upsertAll(aggregates)

            val completedAt = timeProvider.now()
            val status = if (readResult.errors.isEmpty()) {
                SyncStatus.SUCCESS
            } else {
                SyncStatus.PARTIAL
            }
            val run = SyncRun(
                id = UUID.randomUUID().toString(),
                sourceId = HEALTH_CONNECT_SOURCE_ID,
                startedAt = startedAt,
                endedAt = completedAt,
                status = status,
                recordsRead = readResult.records.size,
                message = readResult.errors.joinToString(separator = " | ").ifBlank { null },
            )
            syncRepository.add(run)
            sourceRepository.upsert(
                healthConnectSource(
                    status = SourceStatus.CONNECTED,
                    lastSyncAt = completedAt,
                    lastError = run.message,
                ),
            )
            AppResult.Success(run)
        } catch (t: Throwable) {
            val failedAt = timeProvider.now()
            sourceRepository.upsert(
                healthConnectSource(
                    status = SourceStatus.ERROR,
                    lastSyncAt = failedAt,
                    lastError = t.message ?: "Unexpected sync error",
                ),
            )
            AppResult.Failure(
                UnexpectedError(t.message ?: "Unexpected sync error"),
            )
        }
    }

    private suspend fun ensureHealthConnectSource(now: java.time.Instant) {
        if (sourceRepository.byId(HEALTH_CONNECT_SOURCE_ID) != null) return
        sourceRepository.upsert(
            healthConnectSource(
                status = SourceStatus.DISCONNECTED,
                lastSyncAt = now,
                lastError = null,
            ),
        )
    }

    private fun healthConnectSource(
        status: SourceStatus,
        lastSyncAt: java.time.Instant?,
        lastError: String?,
    ): DataSource =
        DataSource(
            id = HEALTH_CONNECT_SOURCE_ID,
            type = SourceType.HEALTH_CONNECT,
            displayName = "Health Connect",
            status = status,
            priority = 0,
            lastSyncAt = lastSyncAt,
            lastError = lastError,
        )

    companion object {
        const val HEALTH_CONNECT_SOURCE_ID = "health_connect"
    }
}
