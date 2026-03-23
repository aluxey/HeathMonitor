package com.cyberdoc.app.domain.model

import java.time.Instant
import java.time.LocalDate

data class UserProfile(
    val id: String,
    val timezone: String,
    val weightUnit: String,
    val energyUnit: String,
    val createdAt: Instant,
    val updatedAt: Instant,
)

data class DataSource(
    val id: String,
    val type: SourceType,
    val displayName: String,
    val status: SourceStatus,
    val priority: Int,
    val lastSyncAt: Instant?,
    val lastError: String?,
)

data class MetricRecord(
    val id: String,
    val metricType: MetricType,
    val value: Double,
    val unit: String,
    val startAt: Instant,
    val endAt: Instant,
    val sourceId: String,
    val externalId: String?,
    val isManual: Boolean,
    val createdAt: Instant,
)

data class DailyAggregate(
    val id: String,
    val date: LocalDate,
    val metricType: MetricType,
    val value: Double,
    val unit: String,
    val sourceId: String,
    val qualityFlag: QualityFlag,
    val computedAt: Instant,
)

data class Goal(
    val id: String,
    val metricType: MetricType,
    val targetValue: Double,
    val periodType: PeriodType,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val isActive: Boolean,
)

data class SyncRun(
    val id: String,
    val sourceId: String,
    val startedAt: Instant,
    val endedAt: Instant,
    val status: SyncStatus,
    val recordsRead: Int,
    val message: String?,
)

data class DashboardMetric(
    val metricType: MetricType,
    val value: Double,
    val unit: String,
    val trendPercent: Double,
    val goalTarget: Double?,
)

data class DashboardSnapshot(
    val generatedAt: Instant,
    val metrics: List<DashboardMetric>,
    val sources: List<DataSource>,
)
