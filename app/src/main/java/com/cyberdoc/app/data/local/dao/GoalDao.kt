package com.cyberdoc.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.cyberdoc.app.data.local.entity.GoalEntity

@Dao
interface GoalDao {
    @Query("SELECT * FROM goal WHERE isActive = 1")
    suspend fun activeGoals(): List<GoalEntity>

    @Query("SELECT * FROM goal WHERE metricType = :metricType AND isActive = 1 LIMIT 1")
    suspend fun activeGoal(metricType: String): GoalEntity?

    @Upsert
    suspend fun upsert(entity: GoalEntity)
}
