package com.cyberdoc.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cyberdoc.app.data.local.entity.DailyAggregateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyAggregateDao {
    @Query("SELECT * FROM daily_aggregate WHERE date = :date ORDER BY metric_type ASC")
    fun observeByDate(date: String): Flow<List<DailyAggregateEntity>>

    @Query("DELETE FROM daily_aggregate WHERE source_id IS NULL OR source_id != :manualSourceId")
    suspend fun deleteImported(manualSourceId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<DailyAggregateEntity>)
}
