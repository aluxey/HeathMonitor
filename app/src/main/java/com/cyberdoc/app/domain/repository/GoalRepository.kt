package com.cyberdoc.app.domain.repository

import com.cyberdoc.app.data.local.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    fun observeGoals(): Flow<List<GoalEntity>>
}
