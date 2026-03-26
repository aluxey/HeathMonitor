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
            "WHERE endAtEpochMillis > :fromEpochMillis AND startAtEpochMillis < :toEpochMillis",
    )
    suspend fun byDay(fromEpochMillis: Long, toEpochMillis: Long): List<MetricRecordEntity>

    @Query("SELECT * FROM metric_record WHERE metricType = :metricType ORDER BY endAtEpochMillis DESC LIMIT 1")
    suspend fun latest(metricType: String): MetricRecordEntity?

    @Query(
        "SELECT * FROM metric_record " +
            "WHERE metricType = :metricType AND sourceId = :sourceId " +
            "ORDER BY endAtEpochMillis DESC LIMIT 1",
    )
    suspend fun latestBySource(metricType: String, sourceId: String): MetricRecordEntity?

    @Query(
        "SELECT * FROM metric_record " +
            "WHERE metricType = :metricType " +
            "AND endAtEpochMillis > :fromEpochMillis " +
            "AND startAtEpochMillis < :toEpochMillis " +
            "ORDER BY startAtEpochMillis ASC, endAtEpochMillis ASC",
    )
    suspend fun byMetric(
        metricType: String,
        fromEpochMillis: Long,
        toEpochMillis: Long,
    ): List<MetricRecordEntity>

    @Query(
        "SELECT * FROM metric_record " +
            "WHERE metricType = :metricType AND sourceId = :sourceId " +
            "AND endAtEpochMillis > :fromEpochMillis " +
            "AND startAtEpochMillis < :toEpochMillis " +
            "ORDER BY startAtEpochMillis ASC, endAtEpochMillis ASC",
    )
    suspend fun byMetricAndSource(
        metricType: String,
        sourceId: String,
        fromEpochMillis: Long,
        toEpochMillis: Long,
    ): List<MetricRecordEntity>

    @Query("SELECT DISTINCT sourceId FROM metric_record WHERE metricType = :metricType ORDER BY sourceId ASC")
    suspend fun sourceIdsByMetric(metricType: String): List<String>

    @Query(
        "DELETE FROM metric_record " +
            "WHERE isManual = 0 AND endAtEpochMillis > :fromEpochMillis AND startAtEpochMillis < :toEpochMillis",
    )
    suspend fun deleteImportedInRange(fromEpochMillis: Long, toEpochMillis: Long)
}
