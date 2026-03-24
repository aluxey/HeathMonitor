package com.cyberdoc.app.domain.usecase

import com.cyberdoc.app.domain.model.GoalProgress
import com.cyberdoc.app.domain.repository.DashboardRepository
import com.cyberdoc.app.domain.repository.GoalRepository

class GetGoalProgressUseCase(
    private val goalRepository: GoalRepository,
    private val dashboardRepository: DashboardRepository,
) {
    suspend operator fun invoke(): List<GoalProgress> {
        val metricsByType = dashboardRepository.snapshot().metrics.associateBy { it.metricType }
        return goalRepository.activeGoals()
            .sortedBy { it.metricType.name }
            .map { goal ->
                GoalProgress(
                    goal = goal,
                    currentValue = metricsByType[goal.metricType]?.value ?: 0.0,
                )
            }
    }
}
