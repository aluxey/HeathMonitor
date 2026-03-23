package com.cyberdoc.app.data.repository

import com.cyberdoc.app.data.inmemory.InMemoryStore
import com.cyberdoc.app.domain.model.Goal
import com.cyberdoc.app.domain.model.MetricType
import com.cyberdoc.app.domain.repository.GoalRepository

class InMemoryGoalRepository(
    private val store: InMemoryStore,
) : GoalRepository {
    override suspend fun activeGoals(): List<Goal> =
        store.goals.filter { it.isActive }

    override suspend fun upsert(goal: Goal) {
        val index = store.goals.indexOfFirst { it.id == goal.id }
        if (index == -1) {
            store.goals += goal
        } else {
            store.goals[index] = goal
        }
    }

    override suspend fun activeGoal(metricType: MetricType): Goal? =
        store.goals.firstOrNull { it.metricType == metricType && it.isActive }
}
