package com.cyberdoc.app.data.repository

import com.cyberdoc.app.data.local.dao.GoalDao
import com.cyberdoc.app.data.local.entity.GoalEntity
import com.cyberdoc.app.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow

class DefaultGoalRepository(
    private val goalDao: GoalDao,
) : GoalRepository {
    override fun observeGoals(): Flow<List<GoalEntity>> = goalDao.observeActiveGoals()
}
