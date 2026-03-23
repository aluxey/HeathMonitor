package com.cyberdoc.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.cyberdoc.app.data.local.entity.DataSourceEntity

@Dao
interface DataSourceDao {
    @Query("SELECT * FROM data_source ORDER BY priority ASC")
    suspend fun all(): List<DataSourceEntity>

    @Query("SELECT * FROM data_source WHERE id = :id LIMIT 1")
    suspend fun byId(id: String): DataSourceEntity?

    @Upsert
    suspend fun upsert(entity: DataSourceEntity)
}
