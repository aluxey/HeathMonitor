package com.cyberdoc.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cyberdoc.app.data.local.entity.SyncRunEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncRunDao {
    @Query("SELECT * FROM sync_run ORDER BY started_at DESC")
    fun observeAll(): Flow<List<SyncRunEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SyncRunEntity)
}
