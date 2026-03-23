package com.cyberdoc.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.cyberdoc.app.data.local.entity.SyncRunEntity

@Dao
interface SyncRunDao {
    @Upsert
    suspend fun upsert(entity: SyncRunEntity)

    @Query("SELECT * FROM sync_run ORDER BY endedAtEpochMillis DESC LIMIT :limit")
    suspend fun latest(limit: Int): List<SyncRunEntity>
}
