package com.cyberdoc.app.domain.usecase

import androidx.room.withTransaction
import com.cyberdoc.app.data.local.CyberDocDatabase
import com.cyberdoc.app.data.local.entity.DailyAggregateEntity
import com.cyberdoc.app.data.local.entity.DataSourceEntity
import com.cyberdoc.app.data.local.entity.MetricRecordEntity
import com.cyberdoc.app.data.local.entity.SyncRunEntity
import com.cyberdoc.app.domain.model.MetricType
import com.cyberdoc.app.domain.model.QualityFlag
import com.cyberdoc.app.domain.model.SourceStatus
import com.cyberdoc.app.domain.model.SourceType
import com.cyberdoc.app.domain.model.SyncStatus
import com.cyberdoc.app.integration.healthconnect.HealthConnectGateway
import com.cyberdoc.app.integration.healthconnect.HealthConnectRawRecord
import com.cyberdoc.app.integration.healthconnect.HealthConnectSyncPayload
import java.time.Instant
import java.time.ZoneId
import java.util.UUID

class SyncHealthDataUseCase(
    private val database: CyberDocDatabase,
    private val gateway: HealthConnectGateway,
) {
    suspend operator fun invoke() {
        val now = System.currentTimeMillis()
        val payload = gateway.readRecentData(daysBack = 30)
        val syncSucceeded = payload.records.isNotEmpty()
        val status = when {
            payload.availability != com.cyberdoc.app.domain.model.HealthConnectAvailability.AVAILABLE ->
                SourceStatus.UNAVAILABLE
            payload.grantedPermissions.isEmpty() -> SourceStatus.NEEDS_PERMISSION
            else -> SourceStatus.CONNECTED
        }
        val sourceEntities = buildSourceEntities(payload, now)
        val metricRecords = payload.records.map(::toMetricRecordEntity)
        val aggregates = buildDailyAggregates(payload.records, now)

        database.withTransaction {
            val current = database.dataSourceDao().getById(SeedDemoDataUseCase.HEALTH_CONNECT_SOURCE_ID)
                ?: DataSourceEntity(
                    id = SeedDemoDataUseCase.HEALTH_CONNECT_SOURCE_ID,
                    type = SourceType.HEALTH_CONNECT,
                    displayName = "Health Connect",
                    status = SourceStatus.NEEDS_PERMISSION,
                    priority = 1,
                    lastSyncAt = null,
                    lastError = null,
                )

            val updatedSource = current.copy(
                status = status,
                lastSyncAt = if (payload.grantedPermissions.isNotEmpty()) now else current.lastSyncAt,
                lastError = when {
                    payload.availability != com.cyberdoc.app.domain.model.HealthConnectAvailability.AVAILABLE ->
                        "Health Connect indisponible sur cet appareil ou profil"
                    payload.grantedPermissions.isEmpty() ->
                        "Aucune permission Health Connect accordee"
                    payload.records.isEmpty() ->
                        "Aucune donnee lue sur la fenetre de 30 jours"
                    else -> null
                },
            )
            database.dataSourceDao().upsertAll(listOf(updatedSource) + sourceEntities)
            database.metricRecordDao().deleteImported()
            database.dailyAggregateDao().deleteImported(SeedDemoDataUseCase.MANUAL_SOURCE_ID)
            if (metricRecords.isNotEmpty()) {
                database.metricRecordDao().upsertAll(metricRecords)
            }
            if (aggregates.isNotEmpty()) {
                database.dailyAggregateDao().upsertAll(aggregates)
            }

            database.syncRunDao().insert(
                SyncRunEntity(
                    id = UUID.randomUUID().toString(),
                    sourceId = SeedDemoDataUseCase.HEALTH_CONNECT_SOURCE_ID,
                    startedAt = now,
                    endedAt = now,
                    status = when {
                        syncSucceeded -> SyncStatus.SUCCESS
                        payload.grantedPermissions.isNotEmpty() -> SyncStatus.PARTIAL_SUCCESS
                        else -> SyncStatus.FAILURE
                    },
                    recordsRead = payload.records.size,
                    message = if (syncSucceeded) {
                        "Import Health Connect termine sur 30 jours"
                    } else if (payload.grantedPermissions.isNotEmpty()) {
                        "Health Connect accessible mais aucune donnee exploitable n'a ete importee"
                    } else {
                        "Impossible de lancer la synchro sans Health Connect disponible et autorise"
                    },
                ),
            )
        }
    }

    private fun buildSourceEntities(
        payload: HealthConnectSyncPayload,
        now: Long,
    ): List<DataSourceEntity> =
        payload.records
            .map { it.sourcePackageName }
            .distinct()
            .map { packageName ->
                DataSourceEntity(
                    id = sourceIdForPackage(packageName),
                    type = SourceType.HEALTH_CONNECT,
                    displayName = displayNameForPackage(packageName),
                    status = SourceStatus.CONNECTED,
                    priority = sourcePriority(packageName),
                    lastSyncAt = now,
                    lastError = null,
                )
            }

    private fun toMetricRecordEntity(record: HealthConnectRawRecord): MetricRecordEntity =
        MetricRecordEntity(
            id = "${record.metricType.name}:${record.externalId}",
            metricType = record.metricType,
            value = record.value,
            unit = record.unit,
            startAt = record.startAt,
            endAt = record.endAt,
            sourceId = sourceIdForPackage(record.sourcePackageName),
            externalId = record.externalId,
            isManual = false,
            createdAt = System.currentTimeMillis(),
        )

    private fun buildDailyAggregates(
        records: List<HealthConnectRawRecord>,
        computedAt: Long,
    ): List<DailyAggregateEntity> =
        records
            .groupBy { AggregateBucket(dateForAggregation(it), it.metricType, it.sourcePackageName) }
            .mapValues { (_, bucketRecords) -> summarize(bucketRecords) }
            .entries
            .groupBy { (bucket, _) -> bucket.date to bucket.metricType }
            .mapNotNull { (dayMetric, candidates) ->
                val (date, metricType) = dayMetric
                val selected = candidates.minWithOrNull(
                    compareBy<Map.Entry<AggregateBucket, AggregateSummary>>(
                        { metricPriority(metricType, it.key.sourcePackageName) },
                        { -it.value.recordCount },
                        { -it.value.lastTimestamp },
                    ),
                ) ?: return@mapNotNull null

                DailyAggregateEntity(
                    id = "${date}_${metricType.name}",
                    date = date,
                    metricType = metricType,
                    value = selected.value.value,
                    unit = selected.value.unit,
                    sourceId = sourceIdForPackage(selected.key.sourcePackageName),
                    qualityFlag = QualityFlag.OK,
                    computedAt = computedAt,
                )
            }
            .sortedWith(compareBy({ it.date }, { it.metricType.name }))

    private fun summarize(records: List<HealthConnectRawRecord>): AggregateSummary {
        val sorted = records.sortedBy { it.endAt }
        val metricType = sorted.first().metricType
        return when (metricType) {
            MetricType.WEIGHT -> {
                val latest = sorted.last()
                AggregateSummary(
                    value = latest.value,
                    unit = latest.unit,
                    recordCount = sorted.size,
                    lastTimestamp = latest.endAt,
                )
            }
            else -> AggregateSummary(
                value = sorted.sumOf { it.value },
                unit = sorted.first().unit,
                recordCount = sorted.size,
                lastTimestamp = sorted.maxOf { it.endAt },
            )
        }
    }

    private fun dateForAggregation(record: HealthConnectRawRecord): String =
        Instant.ofEpochMilli(
            when (record.metricType) {
                MetricType.SLEEP_DURATION -> record.endAt
                else -> record.endAt
            },
        ).atZone(ZoneId.systemDefault())
            .toLocalDate()
            .toString()

    private fun sourceIdForPackage(packageName: String): String =
        "source_pkg_${packageName.replace('.', '_')}"

    private fun displayNameForPackage(packageName: String): String = when {
        packageName.contains("zepp", ignoreCase = true) ||
            packageName.contains("amazfit", ignoreCase = true) -> "Zepp"
        packageName.contains("samsung", ignoreCase = true) -> "Samsung Health"
        packageName.contains("yazio", ignoreCase = true) -> "Yazio"
        else -> packageName
    }

    private fun sourcePriority(packageName: String): Int = when {
        packageName.contains("zepp", ignoreCase = true) ||
            packageName.contains("amazfit", ignoreCase = true) -> 1
        packageName.contains("samsung", ignoreCase = true) -> 2
        packageName.contains("yazio", ignoreCase = true) -> 3
        else -> 50
    }

    private fun metricPriority(metricType: MetricType, packageName: String): Int = when (metricType) {
        MetricType.CALORIES_IN -> when {
            packageName.contains("yazio", ignoreCase = true) -> 1
            packageName.contains("samsung", ignoreCase = true) -> 2
            packageName.contains("zepp", ignoreCase = true) ||
                packageName.contains("amazfit", ignoreCase = true) -> 3
            else -> 50
        }
        else -> sourcePriority(packageName)
    }

    private data class AggregateBucket(
        val date: String,
        val metricType: MetricType,
        val sourcePackageName: String,
    )

    private data class AggregateSummary(
        val value: Double,
        val unit: String,
        val recordCount: Int,
        val lastTimestamp: Long,
    )
}
