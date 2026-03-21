package com.cyberdoc.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cyberdoc.app.domain.model.SourceStatus
import com.cyberdoc.app.domain.model.SourceType

@Entity(tableName = "data_source")
data class DataSourceEntity(
    @PrimaryKey val id: String,
    val type: SourceType,
    @ColumnInfo(name = "display_name") val displayName: String,
    val status: SourceStatus,
    val priority: Int,
    @ColumnInfo(name = "last_sync_at") val lastSyncAt: Long?,
    @ColumnInfo(name = "last_error") val lastError: String?,
)
