package com.cyberdoc.app.data.repository.room

import com.cyberdoc.app.data.local.dao.GoalDao
import com.cyberdoc.app.domain.model.Goal
import com.cyberdoc.app.domain.model.MetricType
import com.cyberdoc.app.domain.repository.GoalRepository

class RoomGoalRepository(
    private val dao: GoalDao,
) : GoalRepository {
    override suspend fun activeGoals(): List<Goal> =
        dao.activeGoals().map { it.toDomain() }

    override suspend fun upsert(goal: Goal) {
        dao.upsert(goal.toEntity())
    }

    override suspend fun activeGoal(metricType: MetricType): Goal? =
        dao.activeGoal(metricType.name)?.toDomain()
}
