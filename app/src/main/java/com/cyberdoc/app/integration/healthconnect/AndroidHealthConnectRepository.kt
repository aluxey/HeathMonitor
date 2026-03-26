package com.cyberdoc.app.integration.healthconnect

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.HydrationRecord
import androidx.health.connect.client.records.NutritionRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant

class AndroidHealthConnectRepository(
    context: Context,
) : HealthConnectRepository {
    private val appContext = context.applicationContext

    override suspend fun availability(): HealthConnectAvailability =
        when (HealthConnectClient.getSdkStatus(appContext, PROVIDER_PACKAGE_NAME)) {
            HealthConnectClient.SDK_AVAILABLE -> HealthConnectAvailability.AVAILABLE
            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> HealthConnectAvailability.NOT_INSTALLED
            else -> HealthConnectAvailability.NOT_SUPPORTED
        }

    override suspend fun grantedDataTypes(): Set<HealthDataType> {
        if (availability() != HealthConnectAvailability.AVAILABLE) return emptySet()
        val permissions = client().permissionController.getGrantedPermissions()
        return permissions.mapNotNullTo(mutableSetOf()) { permission ->
            HealthConnectPermissions.toHealthDataType(permission)
        }
    }

    override suspend fun readRecords(from: Instant, to: Instant): HealthConnectReadResult {
        val currentAvailability = availability()
        if (currentAvailability != HealthConnectAvailability.AVAILABLE) {
            return HealthConnectReadResult(
                availability = currentAvailability,
                grantedDataTypes = emptySet(),
                records = emptyList(),
                errors = listOf("Health Connect unavailable: $currentAvailability"),
            )
        }

        val grantedPermissions = client().permissionController.getGrantedPermissions()
        val grantedDataTypes = grantedPermissions.mapNotNullTo(mutableSetOf()) {
            HealthConnectPermissions.toHealthDataType(it)
        }
        if (grantedPermissions.isEmpty()) {
            return HealthConnectReadResult(
                availability = currentAvailability,
                grantedDataTypes = emptySet(),
                records = emptyList(),
                errors = listOf("No Health Connect permission granted"),
            )
        }

        val errors = mutableListOf<String>()
        val records = mutableListOf<HealthConnectRawRecord>()

        if (grantedPermissions.contains(HealthConnectPermissions.readSteps)) {
            runCatching { readSteps(from, to) }
                .onSuccess { records += it }
                .onFailure { errors += "steps: ${it.message ?: "read error"}" }
        }
        if (grantedPermissions.contains(HealthConnectPermissions.readSleep)) {
            runCatching { readSleep(from, to) }
                .onSuccess { records += it }
                .onFailure { errors += "sleep: ${it.message ?: "read error"}" }
        }
        if (grantedPermissions.contains(HealthConnectPermissions.readExercise)) {
            runCatching { readExercise(from, to) }
                .onSuccess { records += it }
                .onFailure { errors += "exercise: ${it.message ?: "read error"}" }
        }
        if (grantedPermissions.contains(HealthConnectPermissions.readWeight)) {
            runCatching { readWeight(from, to) }
                .onSuccess { records += it }
                .onFailure { errors += "weight: ${it.message ?: "read error"}" }
        }
        if (grantedPermissions.contains(HealthConnectPermissions.readHydration)) {
            runCatching { readHydration(from, to) }
                .onSuccess { records += it }
                .onFailure { errors += "hydration: ${it.message ?: "read error"}" }
        }
        if (grantedPermissions.contains(HealthConnectPermissions.readNutrition)) {
            runCatching { readNutrition(from, to) }
                .onSuccess { records += it }
                .onFailure { errors += "nutrition: ${it.message ?: "read error"}" }
        }
        if (grantedPermissions.contains(HealthConnectPermissions.readHeartRate)) {
            runCatching { readHeartRate(from, to) }
                .onSuccess { records += it }
                .onFailure { errors += "heart_rate: ${it.message ?: "read error"}" }
        }

        return HealthConnectReadResult(
            availability = currentAvailability,
            grantedDataTypes = grantedDataTypes,
            records = records,
            errors = errors,
        )
    }

    private suspend fun readSteps(from: Instant, to: Instant): List<HealthConnectRawRecord> =
        client().readRecords(
            ReadRecordsRequest(
                recordType = StepsRecord::class,
                timeRangeFilter = TimeRangeFilter.between(from, to),
            ),
        ).records.map { record ->
            HealthConnectRawRecord(
                dataType = HealthDataType.STEPS,
                value = record.count.toDouble(),
                unit = "count",
                startAt = record.startTime,
                endAt = record.endTime,
                sourceAppId = record.metadata.dataOrigin.packageName,
                sourceAppName = record.metadata.dataOrigin.packageName,
                externalId = stableExternalId(
                    fallbackType = HealthDataType.STEPS,
                    sourceAppId = record.metadata.dataOrigin.packageName,
                    startAt = record.startTime,
                    endAt = record.endTime,
                    value = record.count.toDouble(),
                    unit = "count",
                    clientRecordId = record.metadata.clientRecordId,
                ),
            )
        }

    private suspend fun readSleep(from: Instant, to: Instant): List<HealthConnectRawRecord> =
        client().readRecords(
            ReadRecordsRequest(
                recordType = SleepSessionRecord::class,
                timeRangeFilter = TimeRangeFilter.between(from, to),
            ),
        ).records.map { record ->
            val hours = java.time.Duration.between(record.startTime, record.endTime).toMinutes().toDouble() / 60.0
            HealthConnectRawRecord(
                dataType = HealthDataType.SLEEP,
                value = hours,
                unit = "hour",
                startAt = record.startTime,
                endAt = record.endTime,
                sourceAppId = record.metadata.dataOrigin.packageName,
                sourceAppName = record.metadata.dataOrigin.packageName,
                externalId = stableExternalId(
                    fallbackType = HealthDataType.SLEEP,
                    sourceAppId = record.metadata.dataOrigin.packageName,
                    startAt = record.startTime,
                    endAt = record.endTime,
                    value = hours,
                    unit = "hour",
                    clientRecordId = record.metadata.clientRecordId,
                ),
            )
        }

    private suspend fun readExercise(from: Instant, to: Instant): List<HealthConnectRawRecord> =
        client().readRecords(
            ReadRecordsRequest(
                recordType = ExerciseSessionRecord::class,
                timeRangeFilter = TimeRangeFilter.between(from, to),
            ),
        ).records.map { record ->
            val durationMinutes = java.time.Duration.between(record.startTime, record.endTime).toMinutes().toDouble()
            HealthConnectRawRecord(
                dataType = HealthDataType.EXERCISE,
                value = durationMinutes,
                unit = "minute",
                startAt = record.startTime,
                endAt = record.endTime,
                sourceAppId = record.metadata.dataOrigin.packageName,
                sourceAppName = record.metadata.dataOrigin.packageName,
                externalId = stableExternalId(
                    fallbackType = HealthDataType.EXERCISE,
                    sourceAppId = record.metadata.dataOrigin.packageName,
                    startAt = record.startTime,
                    endAt = record.endTime,
                    value = durationMinutes,
                    unit = "minute",
                    clientRecordId = record.metadata.clientRecordId,
                ),
            )
        }

    private suspend fun readWeight(from: Instant, to: Instant): List<HealthConnectRawRecord> =
        client().readRecords(
            ReadRecordsRequest(
                recordType = WeightRecord::class,
                timeRangeFilter = TimeRangeFilter.between(from, to),
            ),
        ).records.map { record ->
            HealthConnectRawRecord(
                dataType = HealthDataType.WEIGHT,
                value = record.weight.inKilograms,
                unit = "kg",
                startAt = record.time,
                endAt = record.time,
                sourceAppId = record.metadata.dataOrigin.packageName,
                sourceAppName = record.metadata.dataOrigin.packageName,
                externalId = stableExternalId(
                    fallbackType = HealthDataType.WEIGHT,
                    sourceAppId = record.metadata.dataOrigin.packageName,
                    startAt = record.time,
                    endAt = record.time,
                    value = record.weight.inKilograms,
                    unit = "kg",
                    clientRecordId = record.metadata.clientRecordId,
                ),
            )
        }

    private suspend fun readHydration(from: Instant, to: Instant): List<HealthConnectRawRecord> =
        client().readRecords(
            ReadRecordsRequest(
                recordType = HydrationRecord::class,
                timeRangeFilter = TimeRangeFilter.between(from, to),
            ),
        ).records.map { record ->
            HealthConnectRawRecord(
                dataType = HealthDataType.HYDRATION,
                value = record.volume.inLiters,
                unit = "l",
                startAt = record.startTime,
                endAt = record.endTime,
                sourceAppId = record.metadata.dataOrigin.packageName,
                sourceAppName = record.metadata.dataOrigin.packageName,
                externalId = stableExternalId(
                    fallbackType = HealthDataType.HYDRATION,
                    sourceAppId = record.metadata.dataOrigin.packageName,
                    startAt = record.startTime,
                    endAt = record.endTime,
                    value = record.volume.inLiters,
                    unit = "l",
                    clientRecordId = record.metadata.clientRecordId,
                ),
            )
        }

    private suspend fun readNutrition(from: Instant, to: Instant): List<HealthConnectRawRecord> =
        client().readRecords(
            ReadRecordsRequest(
                recordType = NutritionRecord::class,
                timeRangeFilter = TimeRangeFilter.between(from, to),
            ),
        ).records.mapNotNull { record ->
            val energy = record.energy?.inKilocalories ?: return@mapNotNull null
            HealthConnectRawRecord(
                dataType = HealthDataType.CALORIES_IN,
                value = energy,
                unit = "kcal",
                startAt = record.startTime,
                endAt = record.endTime,
                sourceAppId = record.metadata.dataOrigin.packageName,
                sourceAppName = record.metadata.dataOrigin.packageName,
                externalId = stableExternalId(
                    fallbackType = HealthDataType.CALORIES_IN,
                    sourceAppId = record.metadata.dataOrigin.packageName,
                    startAt = record.startTime,
                    endAt = record.endTime,
                    value = energy,
                    unit = "kcal",
                    clientRecordId = record.metadata.clientRecordId,
                ),
            )
        }

    private suspend fun readHeartRate(from: Instant, to: Instant): List<HealthConnectRawRecord> =
        client().readRecords(
            ReadRecordsRequest(
                recordType = HeartRateRecord::class,
                timeRangeFilter = TimeRangeFilter.between(from, to),
            ),
        ).records.mapNotNull { record ->
            val latestSample = record.samples.maxByOrNull { it.time } ?: return@mapNotNull null
            HealthConnectRawRecord(
                dataType = HealthDataType.HEART_RATE,
                value = latestSample.beatsPerMinute.toDouble(),
                unit = "bpm",
                startAt = record.startTime,
                endAt = record.endTime,
                sourceAppId = record.metadata.dataOrigin.packageName,
                sourceAppName = record.metadata.dataOrigin.packageName,
                externalId = stableExternalId(
                    fallbackType = HealthDataType.HEART_RATE,
                    sourceAppId = record.metadata.dataOrigin.packageName,
                    startAt = record.startTime,
                    endAt = record.endTime,
                    value = latestSample.beatsPerMinute.toDouble(),
                    unit = "bpm",
                    clientRecordId = record.metadata.clientRecordId,
                ),
            )
        }

    private fun stableExternalId(
        fallbackType: HealthDataType,
        sourceAppId: String?,
        startAt: Instant,
        endAt: Instant,
        value: Double,
        unit: String,
        clientRecordId: String?,
    ): String =
        clientRecordId?.takeIf { it.isNotBlank() }
            ?: listOf(
                fallbackType.name,
                sourceAppId.orEmpty(),
                startAt.toEpochMilli().toString(),
                endAt.toEpochMilli().toString(),
                unit,
                value.toString(),
            ).joinToString(separator = "|")

    private fun client(): HealthConnectClient =
        HealthConnectClient.getOrCreate(appContext)

    companion object {
        private const val PROVIDER_PACKAGE_NAME = "com.google.android.apps.healthdata"
    }
}
