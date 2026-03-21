package com.cyberdoc.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cyberdoc.app.domain.model.SyncStatus

@Entity(tableName = "sync_run")
data class SyncRunEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "source_id") val sourceId: String,
    @ColumnInfo(name = "started_at") val startedAt: Long,
    @ColumnInfo(name = "ended_at") val endedAt: Long?,
    val status: SyncStatus,
    @ColumnInfo(name = "records_read") val recordsRead: Int,
    val message: String?,
)
