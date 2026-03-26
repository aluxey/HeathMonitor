package com.cyberdoc.app.domain.model

data class MetricSourceOption(
    val sourceId: String,
    val displayName: String,
)

data class MetricSourceSetting(
    val metricType: MetricType,
    val selectedSourceId: String?,
    val effectiveSourceId: String?,
    val options: List<MetricSourceOption>,
)
