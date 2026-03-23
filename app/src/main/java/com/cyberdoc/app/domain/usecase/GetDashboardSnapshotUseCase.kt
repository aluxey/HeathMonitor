package com.cyberdoc.app.domain.usecase

import com.cyberdoc.app.domain.model.DashboardSnapshot
import com.cyberdoc.app.domain.repository.DashboardRepository

class GetDashboardSnapshotUseCase(
    private val dashboardRepository: DashboardRepository,
) {
    suspend operator fun invoke(): DashboardSnapshot = dashboardRepository.snapshot()
}
