package com.cyberdoc.app.domain.repository

import com.cyberdoc.app.domain.model.DashboardMetric
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

interface DashboardRepository {
    fun observeDashboard(date: LocalDate, periodDays: Int): Flow<List<DashboardMetric>>
}
