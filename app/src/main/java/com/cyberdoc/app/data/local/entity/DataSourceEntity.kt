package com.cyberdoc.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "data_source")
data class DataSourceEntity(
    @PrimaryKey val id: String,
    val type: String,
    val displayName: String,
    val status: String,
    val priority: Int,
    val lastSyncAtEpochMillis: Long?,
    val lastError: String?,
)
