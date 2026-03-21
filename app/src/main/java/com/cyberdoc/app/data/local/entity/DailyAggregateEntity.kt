package com.cyberdoc.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cyberdoc.app.domain.model.MetricType
import com.cyberdoc.app.domain.model.QualityFlag

@Entity(tableName = "daily_aggregate")
data class DailyAggregateEntity(
    @PrimaryKey val id: String,
    val date: String,
    @ColumnInfo(name = "metric_type") val metricType: MetricType,
    val value: Double,
    val unit: String,
    @ColumnInfo(name = "source_id") val sourceId: String?,
    @ColumnInfo(name = "quality_flag") val qualityFlag: QualityFlag,
    @ColumnInfo(name = "computed_at") val computedAt: Long,
)
