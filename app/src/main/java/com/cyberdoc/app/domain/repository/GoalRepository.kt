package com.cyberdoc.app.domain.repository

import com.cyberdoc.app.domain.model.Goal
import com.cyberdoc.app.domain.model.MetricType

interface GoalRepository {
    suspend fun activeGoals(): List<Goal>
    suspend fun upsert(goal: Goal)
    suspend fun activeGoal(metricType: MetricType): Goal?
}
