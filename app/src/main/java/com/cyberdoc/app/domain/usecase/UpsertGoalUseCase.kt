package com.cyberdoc.app.domain.usecase

import com.cyberdoc.app.core.AppResult
import com.cyberdoc.app.core.ValidationError
import com.cyberdoc.app.domain.model.Goal
import com.cyberdoc.app.domain.repository.GoalRepository

class UpsertGoalUseCase(
    private val goalRepository: GoalRepository,
) {
    suspend operator fun invoke(goal: Goal): AppResult<Unit> {
        if (goal.targetValue <= 0) {
            return AppResult.Failure(ValidationError("Goal target must be greater than zero"))
        }
        goalRepository.upsert(goal)
        return AppResult.Success(Unit)
    }
}
