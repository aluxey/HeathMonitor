package com.cyberdoc.app.integration.healthconnect

import java.time.Instant

interface HealthConnectRepository {
    suspend fun availability(): HealthConnectAvailability
    suspend fun grantedDataTypes(): Set<HealthDataType>
    suspend fun readRecords(from: Instant, to: Instant): HealthConnectReadResult
}
