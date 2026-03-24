package com.cyberdoc.app.integration.healthconnect

import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.HydrationRecord
import androidx.health.connect.client.records.NutritionRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord

object HealthConnectPermissions {
    val readSteps = HealthPermission.getReadPermission(StepsRecord::class)
    val readSleep = HealthPermission.getReadPermission(SleepSessionRecord::class)
    val readExercise = HealthPermission.getReadPermission(ExerciseSessionRecord::class)
    val readWeight = HealthPermission.getReadPermission(WeightRecord::class)
    val readHydration = HealthPermission.getReadPermission(HydrationRecord::class)
    val readNutrition = HealthPermission.getReadPermission(NutritionRecord::class)
    val readHeartRate = HealthPermission.getReadPermission(HeartRateRecord::class)

    val requiredReadPermissions: Set<String> = setOf(
        readSteps,
        readSleep,
        readExercise,
        readWeight,
        readHydration,
        readNutrition,
        readHeartRate,
    )

    fun toHealthDataType(permission: String): HealthDataType? =
        when (permission) {
            readSteps -> HealthDataType.STEPS
            readSleep -> HealthDataType.SLEEP
            readExercise -> HealthDataType.EXERCISE
            readWeight -> HealthDataType.WEIGHT
            readHydration -> HealthDataType.HYDRATION
            readNutrition -> HealthDataType.CALORIES_IN
            readHeartRate -> HealthDataType.HEART_RATE
            else -> null
        }

    fun toPermission(dataType: HealthDataType): String =
        when (dataType) {
            HealthDataType.STEPS -> readSteps
            HealthDataType.SLEEP -> readSleep
            HealthDataType.EXERCISE -> readExercise
            HealthDataType.WEIGHT -> readWeight
            HealthDataType.HYDRATION -> readHydration
            HealthDataType.CALORIES_IN -> readNutrition
            HealthDataType.HEART_RATE -> readHeartRate
        }
}
