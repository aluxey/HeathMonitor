package com.cyberdoc.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "metric_record",
    indices = [
        Index(value = ["metricType", "endAtEpochMillis"]),
        Index(value = ["startAtEpochMillis"]),
        Index(value = ["metricType", "startAtEpochMillis", "endAtEpochMillis", "sourceId", "externalId"], unique = true),
    ],
)
data class MetricRecordEntity(
    @PrimaryKey val id: String,
    val metricType: String,
    val value: Double,
    val unit: String,
    val startAtEpochMillis: Long,
    val endAtEpochMillis: Long,
    val sourceId: String,
    val externalId: String?,
    val isManual: Boolean,
    val createdAtEpochMillis: Long,
)
