package com.cyberdoc.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cyberdoc.app.domain.model.MetricType

@Entity(tableName = "metric_record")
data class MetricRecordEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "metric_type") val metricType: MetricType,
    val value: Double,
    val unit: String,
    @ColumnInfo(name = "start_at") val startAt: Long,
    @ColumnInfo(name = "end_at") val endAt: Long,
    @ColumnInfo(name = "source_id") val sourceId: String,
    @ColumnInfo(name = "external_id") val externalId: String?,
    @ColumnInfo(name = "is_manual") val isManual: Boolean,
    @ColumnInfo(name = "created_at") val createdAt: Long,
)
