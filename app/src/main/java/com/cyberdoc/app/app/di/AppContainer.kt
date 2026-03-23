package com.cyberdoc.app.app.di

import com.cyberdoc.app.domain.repository.DashboardRepository
import com.cyberdoc.app.domain.repository.DailyAggregateRepository
import com.cyberdoc.app.domain.repository.GoalRepository
import com.cyberdoc.app.domain.repository.MetricRepository
import com.cyberdoc.app.domain.repository.SourceRepository
import com.cyberdoc.app.domain.repository.SyncRepository
import com.cyberdoc.app.domain.usecase.BootstrapMvpDataUseCase
import com.cyberdoc.app.domain.usecase.GetDashboardSnapshotUseCase
import com.cyberdoc.app.domain.usecase.RegisterManualMetricUseCase
import com.cyberdoc.app.domain.usecase.SyncHealthConnectDataUseCase
import com.cyberdoc.app.domain.usecase.TriggerSyncUseCase
import com.cyberdoc.app.domain.usecase.UpsertGoalUseCase
import com.cyberdoc.app.integration.healthconnect.HealthConnectRepository

interface AppContainer {
    val dashboardRepository: DashboardRepository
    val dailyAggregateRepository: DailyAggregateRepository
    val goalRepository: GoalRepository
    val metricRepository: MetricRepository
    val sourceRepository: SourceRepository
    val syncRepository: SyncRepository
    val healthConnectRepository: HealthConnectRepository

    val bootstrapMvpDataUseCase: BootstrapMvpDataUseCase
    val getDashboardSnapshotUseCase: GetDashboardSnapshotUseCase
    val upsertGoalUseCase: UpsertGoalUseCase
    val registerManualMetricUseCase: RegisterManualMetricUseCase
    val triggerSyncUseCase: TriggerSyncUseCase
    val syncHealthConnectDataUseCase: SyncHealthConnectDataUseCase
}
