package com.cyberdoc.app.integration.healthconnect

import com.cyberdoc.app.core.TimeProvider
import java.time.temporal.ChronoUnit
import java.util.UUID

class StubHealthConnectRepository(
    private val timeProvider: TimeProvider,
) : HealthConnectRepository {
    override suspend fun availability(): HealthConnectAvailability =
        HealthConnectAvailability.AVAILABLE

    override suspend fun grantedDataTypes(): Set<HealthDataType> =
        setOf(
            HealthDataType.STEPS,
            HealthDataType.SLEEP,
            HealthDataType.EXERCISE,
            HealthDataType.WEIGHT,
            HealthDataType.HYDRATION,
            HealthDataType.CALORIES_IN,
        )

    override suspend fun readRecords(from: java.time.Instant, to: java.time.Instant): HealthConnectReadResult {
        val now = timeProvider.now().truncatedTo(ChronoUnit.MINUTES)
        val records = listOf(
            HealthConnectRawRecord(
                dataType = HealthDataType.STEPS,
                value = 8240.0,
                unit = "count",
                startAt = now.minus(12, ChronoUnit.HOURS),
                endAt = now.minus(1, ChronoUnit.HOURS),
                sourceAppId = "com.google.android.apps.fitness",
                sourceAppName = "Google Fit",
                externalId = UUID.randomUUID().toString(),
            ),
            HealthConnectRawRecord(
                dataType = HealthDataType.SLEEP,
                value = 7.1,
                unit = "hour",
                startAt = now.minus(8, ChronoUnit.HOURS),
                endAt = now.minus(1, ChronoUnit.HOURS),
                sourceAppId = "com.samsung.android.app.health",
                sourceAppName = "Samsung Health",
                externalId = UUID.randomUUID().toString(),
            ),
            HealthConnectRawRecord(
                dataType = HealthDataType.WEIGHT,
                value = 78.4,
                unit = "kg",
                startAt = now.minus(3, ChronoUnit.HOURS),
                endAt = now.minus(3, ChronoUnit.HOURS),
                sourceAppId = "com.myfitnesspal.android",
                sourceAppName = "Yazio",
                externalId = UUID.randomUUID().toString(),
            ),
            HealthConnectRawRecord(
                dataType = HealthDataType.EXERCISE,
                value = 52.0,
                unit = "minute",
                startAt = now.minus(5, ChronoUnit.HOURS),
                endAt = now.minus(4, ChronoUnit.HOURS).plus(52, ChronoUnit.MINUTES),
                sourceAppId = "com.zepp.health",
                sourceAppName = "Zepp",
                externalId = UUID.randomUUID().toString(),
            ),
            HealthConnectRawRecord(
                dataType = HealthDataType.HYDRATION,
                value = 1.3,
                unit = "l",
                startAt = now.minus(10, ChronoUnit.HOURS),
                endAt = now.minus(2, ChronoUnit.HOURS),
                sourceAppId = "com.myfitnesspal.android",
                sourceAppName = "Yazio",
                externalId = UUID.randomUUID().toString(),
            ),
            HealthConnectRawRecord(
                dataType = HealthDataType.CALORIES_IN,
                value = 1980.0,
                unit = "kcal",
                startAt = now.minus(18, ChronoUnit.HOURS),
                endAt = now.minus(1, ChronoUnit.HOURS),
                sourceAppId = "com.myfitnesspal.android",
                sourceAppName = "Yazio",
                externalId = UUID.randomUUID().toString(),
            ),
        )

        val filtered = records.filter { it.endAt >= from && it.endAt <= to }
        return HealthConnectReadResult(
            availability = HealthConnectAvailability.AVAILABLE,
            grantedDataTypes = grantedDataTypes(),
            records = filtered,
            errors = emptyList(),
        )
    }
}
