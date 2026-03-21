package com.cyberdoc.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cyberdoc.app.data.local.entity.MetricRecordEntity

@Dao
interface MetricRecordDao {
    @Query("DELETE FROM metric_record WHERE is_manual = 0")
    suspend fun deleteImported()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: MetricRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<MetricRecordEntity>)
}
