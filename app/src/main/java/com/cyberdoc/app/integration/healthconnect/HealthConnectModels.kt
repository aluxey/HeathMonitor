package com.cyberdoc.app.integration.healthconnect

import java.time.Instant

enum class HealthConnectAvailability {
    AVAILABLE,
    NOT_INSTALLED,
    NOT_SUPPORTED,
}

enum class HealthDataType {
    STEPS,
    SLEEP,
    EXERCISE,
    WEIGHT,
    HYDRATION,
    CALORIES_IN,
    HEART_RATE,
}

data class HealthConnectRawRecord(
    val dataType: HealthDataType,
    val value: Double,
    val unit: String,
    val startAt: Instant,
    val endAt: Instant,
    val sourceAppId: String?,
    val sourceAppName: String?,
    val externalId: String?,
)

data class HealthConnectReadResult(
    val availability: HealthConnectAvailability,
    val grantedDataTypes: Set<HealthDataType>,
    val records: List<HealthConnectRawRecord>,
    val errors: List<String>,
)
