package com.cyberdoc.app.integration.healthconnect

import com.cyberdoc.app.domain.model.MetricType

data class HealthConnectRawRecord(
    val externalId: String,
    val metricType: MetricType,
    val value: Double,
    val unit: String,
    val startAt: Long,
    val endAt: Long,
    val sourcePackageName: String,
)

data class HealthConnectSyncPayload(
    val availability: com.cyberdoc.app.domain.model.HealthConnectAvailability,
    val grantedPermissions: Set<String>,
    val records: List<HealthConnectRawRecord>,
    val importedMetricTypes: Set<MetricType>,
)
