package com.cyberdoc.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.cyberdoc.app.data.local.entity.MetricRecordEntity

@Dao
interface MetricRecordDao {
    @Upsert
    suspend fun upsert(entity: MetricRecordEntity)

    @Upsert
    suspend fun upsertAll(entities: List<MetricRecordEntity>)

    @Query(
        "SELECT * FROM metric_record " +
            "WHERE startAtEpochMillis >= :fromEpochMillis AND startAtEpochMillis < :toEpochMillis",
    )
    suspend fun byDay(fromEpochMillis: Long, toEpochMillis: Long): List<MetricRecordEntity>

    @Query("SELECT * FROM metric_record WHERE metricType = :metricType ORDER BY endAtEpochMillis DESC LIMIT 1")
    suspend fun latest(metricType: String): MetricRecordEntity?
}
