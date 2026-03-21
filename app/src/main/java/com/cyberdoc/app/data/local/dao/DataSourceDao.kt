package com.cyberdoc.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cyberdoc.app.data.local.entity.DataSourceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DataSourceDao {
    @Query("SELECT * FROM data_source ORDER BY priority ASC, display_name ASC")
    fun observeAll(): Flow<List<DataSourceEntity>>

    @Query("SELECT * FROM data_source WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): DataSourceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<DataSourceEntity>)
}
