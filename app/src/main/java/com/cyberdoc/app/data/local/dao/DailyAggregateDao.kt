package com.cyberdoc.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.cyberdoc.app.data.local.entity.DailyAggregateEntity

@Dao
interface DailyAggregateDao {
    @Upsert
    suspend fun upsertAll(entities: List<DailyAggregateEntity>)

    @Query("DELETE FROM daily_aggregate WHERE date >= :fromDate AND date <= :toDate")
    suspend fun deleteByDateRange(fromDate: String, toDate: String)

    @Query(
        "SELECT * FROM daily_aggregate " +
            "WHERE metricType = :metricType ORDER BY date DESC, computedAtEpochMillis DESC LIMIT 1",
    )
    suspend fun latest(metricType: String): DailyAggregateEntity?

    @Query("SELECT * FROM daily_aggregate WHERE date = :date")
    suspend fun byDate(date: String): List<DailyAggregateEntity>

    @Query(
        "SELECT * FROM daily_aggregate " +
            "WHERE metricType = :metricType AND date >= :fromDate AND date <= :toDate ORDER BY date ASC",
    )
    suspend fun trend(metricType: String, fromDate: String, toDate: String): List<DailyAggregateEntity>
}
