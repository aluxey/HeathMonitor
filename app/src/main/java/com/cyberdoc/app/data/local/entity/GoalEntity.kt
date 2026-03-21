package com.cyberdoc.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cyberdoc.app.domain.model.MetricType
import com.cyberdoc.app.domain.model.PeriodType

@Entity(tableName = "goal")
data class GoalEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "metric_type") val metricType: MetricType,
    @ColumnInfo(name = "target_value") val targetValue: Double,
    @ColumnInfo(name = "period_type") val periodType: PeriodType,
    @ColumnInfo(name = "start_date") val startDate: String,
    @ColumnInfo(name = "end_date") val endDate: String?,
    @ColumnInfo(name = "is_active") val isActive: Boolean,
)
