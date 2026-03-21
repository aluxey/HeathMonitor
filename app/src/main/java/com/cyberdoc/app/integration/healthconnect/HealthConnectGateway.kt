package com.cyberdoc.app.integration.healthconnect

import com.cyberdoc.app.domain.model.HealthConnectAvailability

class HealthConnectGateway(
    private val manager: HealthConnectManager,
) {
    suspend fun readRecentData(daysBack: Long): HealthConnectSyncPayload =
        if (manager.availability() == HealthConnectAvailability.AVAILABLE) {
            manager.readRecentData(daysBack)
        } else {
            HealthConnectSyncPayload(
                availability = manager.availability(),
                grantedPermissions = emptySet(),
                records = emptyList(),
                importedMetricTypes = emptySet(),
            )
        }
}
