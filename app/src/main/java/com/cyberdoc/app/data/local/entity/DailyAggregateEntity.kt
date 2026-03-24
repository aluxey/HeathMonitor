package com.cyberdoc.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_aggregate",
    indices = [
        Index(value = ["date", "metricType"], unique = true),
    ],
)
data class DailyAggregateEntity(
    @PrimaryKey val id: String,
    val date: String,
    val metricType: String,
    val value: Double,
    val unit: String,
    val sourceId: String,
    val qualityFlag: String,
    val computedAtEpochMillis: Long,
)
