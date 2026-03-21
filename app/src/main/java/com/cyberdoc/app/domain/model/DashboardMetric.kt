package com.cyberdoc.app.domain.model

data class DashboardMetric(
    val metricType: MetricType,
    val title: String,
    val valueLabel: String,
    val targetLabel: String,
    val freshnessLabel: String,
    val sourceLabel: String,
    val progress: Float,
    val trendLabel: String,
    val qualityFlag: QualityFlag,
)
