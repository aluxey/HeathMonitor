package com.cyberdoc.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sync_run",
    indices = [Index(value = ["endedAtEpochMillis"])],
)
data class SyncRunEntity(
    @PrimaryKey val id: String,
    val sourceId: String,
    val startedAtEpochMillis: Long,
    val endedAtEpochMillis: Long,
    val status: String,
    val recordsRead: Int,
    val message: String?,
)
