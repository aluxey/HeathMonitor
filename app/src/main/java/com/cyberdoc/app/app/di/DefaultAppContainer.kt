package com.cyberdoc.app.app.di

import android.content.Context
import com.cyberdoc.app.core.SystemTimeProvider
import com.cyberdoc.app.data.inmemory.InMemoryStore
import com.cyberdoc.app.data.repository.InMemoryDashboardRepository
import com.cyberdoc.app.data.repository.InMemoryGoalRepository
import com.cyberdoc.app.data.repository.InMemoryMetricRepository
import com.cyberdoc.app.data.repository.InMemorySourceRepository
import com.cyberdoc.app.data.repository.InMemorySyncRepository
import com.cyberdoc.app.domain.repository.DashboardRepository
import com.cyberdoc.app.domain.repository.GoalRepository
import com.cyberdoc.app.domain.repository.MetricRepository
import com.cyberdoc.app.domain.repository.SourceRepository
import com.cyberdoc.app.domain.repository.SyncRepository
import com.cyberdoc.app.domain.usecase.BootstrapMvpDataUseCase
import com.cyberdoc.app.domain.usecase.GetDashboardSnapshotUseCase
import com.cyberdoc.app.domain.usecase.RegisterManualMetricUseCase
import com.cyberdoc.app.domain.usecase.TriggerSyncUseCase
import com.cyberdoc.app.domain.usecase.UpsertGoalUseCase

class DefaultAppContainer(
    @Suppress("UNUSED_PARAMETER") context: Context,
) : AppContainer {
    private val store = InMemoryStore()
    private val clock = SystemTimeProvider

    override val dashboardRepository: DashboardRepository =
        InMemoryDashboardRepository(store = store, timeProvider = clock)

    override val goalRepository: GoalRepository =
        InMemoryGoalRepository(store)

    override val metricRepository: MetricRepository =
        InMemoryMetricRepository(store)

    override val sourceRepository: SourceRepository =
        InMemorySourceRepository(store)

    override val syncRepository: SyncRepository =
        InMemorySyncRepository(store)

    override val bootstrapMvpDataUseCase = BootstrapMvpDataUseCase(
        dashboardRepository = dashboardRepository,
        goalRepository = goalRepository,
        sourceRepository = sourceRepository,
        timeProvider = clock,
    )

    override val getDashboardSnapshotUseCase = GetDashboardSnapshotUseCase(dashboardRepository)
    override val upsertGoalUseCase = UpsertGoalUseCase(goalRepository)
    override val registerManualMetricUseCase = RegisterManualMetricUseCase(metricRepository)
    override val triggerSyncUseCase = TriggerSyncUseCase(syncRepository)
}
