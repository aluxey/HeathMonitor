package com.cyberdoc.app.domain.usecase

import com.cyberdoc.app.core.TimeProvider
import com.cyberdoc.app.domain.model.DashboardSnapshot
import com.cyberdoc.app.domain.model.DataSource
import com.cyberdoc.app.domain.model.Goal
import com.cyberdoc.app.domain.model.MetricType
import com.cyberdoc.app.domain.model.PeriodType
import com.cyberdoc.app.domain.model.SourceStatus
import com.cyberdoc.app.domain.model.SourceType
import com.cyberdoc.app.domain.repository.DashboardRepository
import com.cyberdoc.app.domain.repository.GoalRepository
import com.cyberdoc.app.domain.repository.SourceRepository
import java.time.LocalDate

class BootstrapMvpDataUseCase(
    private val dashboardRepository: DashboardRepository,
    private val goalRepository: GoalRepository,
    private val sourceRepository: SourceRepository,
    private val timeProvider: TimeProvider,
) {
    suspend operator fun invoke() {
        val snapshot: DashboardSnapshot = dashboardRepository.snapshot()
        if (snapshot.sources.isNotEmpty()) return

        // Le backend réel remplacera ce bootstrap in-memory.
        val now = timeProvider.now()
        val source = DataSource(
            id = "manual",
            type = SourceType.MANUAL,
            displayName = "Manual entry",
            status = SourceStatus.CONNECTED,
            priority = 1,
            lastSyncAt = now,
            lastError = null,
        )
        sourceRepository.upsert(source)

        goalRepository.upsert(
            Goal(
                id = "goal_steps",
                metricType = MetricType.STEPS,
                targetValue = 10000.0,
                periodType = PeriodType.DAILY,
                startDate = LocalDate.now(),
                endDate = null,
                isActive = true,
            ),
        )
    }
}
