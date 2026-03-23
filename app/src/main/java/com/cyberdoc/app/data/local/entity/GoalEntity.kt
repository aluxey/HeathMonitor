package com.cyberdoc.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "goal",
    indices = [Index(value = ["metricType", "isActive"])],
)
data class GoalEntity(
    @PrimaryKey val id: String,
    val metricType: String,
    val targetValue: Double,
    val periodType: String,
    val startDate: String,
    val endDate: String?,
    val isActive: Boolean,
)
