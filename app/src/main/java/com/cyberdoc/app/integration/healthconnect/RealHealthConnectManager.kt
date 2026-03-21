package com.cyberdoc.app.integration.healthconnect

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.NutritionRecord
import androidx.health.connect.client.records.Record as HealthRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.cyberdoc.app.domain.model.HealthConnectAvailability
import com.cyberdoc.app.domain.model.MetricType
import java.time.Duration
import java.time.Instant
import kotlin.reflect.KClass

class RealHealthConnectManager(
    private val context: Context,
) : HealthConnectManager {
    private val metricPermissions = mapOf(
        MetricType.STEPS to HealthPermission.getReadPermission(StepsRecord::class),
        MetricType.SLEEP_DURATION to HealthPermission.getReadPermission(SleepSessionRecord::class),
        MetricType.WEIGHT to HealthPermission.getReadPermission(WeightRecord::class),
        MetricType.CALORIES_IN to HealthPermission.getReadPermission(NutritionRecord::class),
        MetricType.EXERCISE_DURATION to HealthPermission.getReadPermission(ExerciseSessionRecord::class),
    )

    private val permissions = setOf(
        *metricPermissions.values.toTypedArray(),
    )

    override fun availability(): HealthConnectAvailability =
        when (HealthConnectClient.getSdkStatus(context, HEALTH_CONNECT_PROVIDER)) {
            HealthConnectClient.SDK_AVAILABLE -> HealthConnectAvailability.AVAILABLE
            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED ->
                HealthConnectAvailability.NOT_INSTALLED
            else -> HealthConnectAvailability.NOT_SUPPORTED
        }

    override fun requiredPermissions(): Set<String> = permissions

    override suspend fun grantedPermissions(): Set<String> {
        if (availability() != HealthConnectAvailability.AVAILABLE) {
            return emptySet()
        }
        val client = HealthConnectClient.getOrCreate(context)
        return client.permissionController.getGrantedPermissions()
    }

    override fun hasAllPermissions(grantedPermissions: Set<String>): Boolean =
        grantedPermissions.containsAll(permissions)

    override suspend fun readRecentData(daysBack: Long): HealthConnectSyncPayload {
        val availability = availability()
        if (availability != HealthConnectAvailability.AVAILABLE) {
            return HealthConnectSyncPayload(
                availability = availability,
                grantedPermissions = emptySet(),
                records = emptyList(),
                importedMetricTypes = emptySet(),
            )
        }

        val grantedPermissions = grantedPermissions()
        val endTime = Instant.now()
        val startTime = endTime.minus(Duration.ofDays(daysBack))
        val client = HealthConnectClient.getOrCreate(context)

        val importedRecords = buildList {
            if (isGranted(grantedPermissions, MetricType.STEPS)) {
                addAll(
                    readAllRecords(client, StepsRecord::class, startTime, endTime).map { record ->
                        HealthConnectRawRecord(
                            externalId = record.metadata.id,
                            metricType = MetricType.STEPS,
                            value = record.count.toDouble(),
                            unit = "count",
                            startAt = record.startTime.toEpochMilli(),
                            endAt = record.endTime.toEpochMilli(),
                            sourcePackageName = record.metadata.dataOrigin.packageName,
                        )
                    },
                )
            }

            if (isGranted(grantedPermissions, MetricType.SLEEP_DURATION)) {
                addAll(
                    readAllRecords(client, SleepSessionRecord::class, startTime, endTime).map { record ->
                        val durationHours =
                            Duration.between(record.startTime, record.endTime).toMinutes().toDouble() / 60.0
                        HealthConnectRawRecord(
                            externalId = record.metadata.id,
                            metricType = MetricType.SLEEP_DURATION,
                            value = durationHours,
                            unit = "hours",
                            startAt = record.startTime.toEpochMilli(),
                            endAt = record.endTime.toEpochMilli(),
                            sourcePackageName = record.metadata.dataOrigin.packageName,
                        )
                    },
                )
            }

            if (isGranted(grantedPermissions, MetricType.WEIGHT)) {
                addAll(
                    readAllRecords(client, WeightRecord::class, startTime, endTime).map { record ->
                        HealthConnectRawRecord(
                            externalId = record.metadata.id,
                            metricType = MetricType.WEIGHT,
                            value = record.weight.inKilograms,
                            unit = "kg",
                            startAt = record.time.toEpochMilli(),
                            endAt = record.time.toEpochMilli(),
                            sourcePackageName = record.metadata.dataOrigin.packageName,
                        )
                    },
                )
            }

            if (isGranted(grantedPermissions, MetricType.CALORIES_IN)) {
                addAll(
                    readAllRecords(client, NutritionRecord::class, startTime, endTime)
                        .mapNotNull { record ->
                            val energy = record.energy ?: return@mapNotNull null
                            HealthConnectRawRecord(
                                externalId = record.metadata.id,
                                metricType = MetricType.CALORIES_IN,
                                value = energy.inKilocalories,
                                unit = "kcal",
                                startAt = record.startTime.toEpochMilli(),
                                endAt = record.endTime.toEpochMilli(),
                                sourcePackageName = record.metadata.dataOrigin.packageName,
                            )
                        },
                )
            }

            if (isGranted(grantedPermissions, MetricType.EXERCISE_DURATION)) {
                addAll(
                    readAllRecords(client, ExerciseSessionRecord::class, startTime, endTime).map { record ->
                        HealthConnectRawRecord(
                            externalId = record.metadata.id,
                            metricType = MetricType.EXERCISE_DURATION,
                            value = Duration.between(record.startTime, record.endTime).toMinutes().toDouble(),
                            unit = "minutes",
                            startAt = record.startTime.toEpochMilli(),
                            endAt = record.endTime.toEpochMilli(),
                            sourcePackageName = record.metadata.dataOrigin.packageName,
                        )
                    },
                )
            }
        }

        return HealthConnectSyncPayload(
            availability = availability,
            grantedPermissions = grantedPermissions,
            records = importedRecords,
            importedMetricTypes = importedRecords.mapTo(linkedSetOf()) { it.metricType },
        )
    }

    private fun isGranted(grantedPermissions: Set<String>, metricType: MetricType): Boolean =
        grantedPermissions.contains(metricPermissions.getValue(metricType))

    private suspend fun <T : HealthRecord> readAllRecords(
        client: HealthConnectClient,
        recordType: KClass<T>,
        startTime: Instant,
        endTime: Instant,
    ): List<T> {
        val records = mutableListOf<T>()
        var pageToken: String? = null

        do {
            val response = client.readRecords(
                ReadRecordsRequest(
                    recordType = recordType,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                    pageToken = pageToken,
                ),
            )
            records += response.records
            pageToken = response.pageToken
        } while (pageToken != null)

        return records
    }

    companion object {
        private const val HEALTH_CONNECT_PROVIDER = "com.google.android.apps.healthdata"
    }
}
