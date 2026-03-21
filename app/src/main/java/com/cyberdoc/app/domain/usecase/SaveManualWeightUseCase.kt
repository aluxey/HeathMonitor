package com.cyberdoc.app.domain.usecase

import androidx.room.withTransaction
import com.cyberdoc.app.data.local.CyberDocDatabase
import com.cyberdoc.app.data.local.entity.DailyAggregateEntity
import com.cyberdoc.app.data.local.entity.DataSourceEntity
import com.cyberdoc.app.data.local.entity.MetricRecordEntity
import com.cyberdoc.app.domain.model.MetricType
import com.cyberdoc.app.domain.model.QualityFlag
import com.cyberdoc.app.domain.model.SourceStatus
import com.cyberdoc.app.domain.model.SourceType
import java.time.Instant
import java.time.ZoneId
import java.util.UUID

class SaveManualWeightUseCase(
    private val database: CyberDocDatabase,
) {
    suspend operator fun invoke(weightKg: Double) {
        val now = System.currentTimeMillis()
        val nowInstant = Instant.ofEpochMilli(now)
        val date = nowInstant.atZone(ZoneId.systemDefault()).toLocalDate().toString()

        database.withTransaction {
            database.dataSourceDao().upsertAll(
                listOf(
                    DataSourceEntity(
                        id = SeedDemoDataUseCase.MANUAL_SOURCE_ID,
                        type = SourceType.MANUAL,
                        displayName = "Saisie manuelle",
                        status = SourceStatus.CONNECTED,
                        priority = 2,
                        lastSyncAt = now,
                        lastError = null,
                    ),
                ),
            )

            database.metricRecordDao().upsert(
                MetricRecordEntity(
                    id = UUID.randomUUID().toString(),
                    metricType = MetricType.WEIGHT,
                    value = weightKg,
                    unit = "kg",
                    startAt = now,
                    endAt = now,
                    sourceId = SeedDemoDataUseCase.MANUAL_SOURCE_ID,
                    externalId = null,
                    isManual = true,
                    createdAt = now,
                ),
            )

            database.dailyAggregateDao().upsertAll(
                listOf(
                    DailyAggregateEntity(
                        id = "${date}_${MetricType.WEIGHT.name}",
                        date = date,
                        metricType = MetricType.WEIGHT,
                        value = weightKg,
                        unit = "kg",
                        sourceId = SeedDemoDataUseCase.MANUAL_SOURCE_ID,
                        qualityFlag = QualityFlag.OK,
                        computedAt = now,
                    ),
                ),
            )
        }
    }
}
