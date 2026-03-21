package com.cyberdoc.app.integration.healthconnect

import com.cyberdoc.app.domain.model.HealthConnectAvailability

interface HealthConnectManager {
    fun availability(): HealthConnectAvailability
    fun requiredPermissions(): Set<String>
    suspend fun grantedPermissions(): Set<String>
    fun hasAllPermissions(grantedPermissions: Set<String>): Boolean
    suspend fun readRecentData(daysBack: Long): HealthConnectSyncPayload
}
