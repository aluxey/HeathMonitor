package com.cyberdoc.app.app.di

import android.content.Context
import com.cyberdoc.app.core.SystemTimeProvider
import com.cyberdoc.app.data.local.CyberDocDatabase
import com.cyberdoc.app.data.repository.room.RoomDailyAggregateRepository
import com.cyberdoc.app.data.repository.room.RoomDashboardRepository
import com.cyberdoc.app.data.repository.room.RoomGoalRepository
import com.cyberdoc.app.data.repository.room.RoomMetricRepository
import com.cyberdoc.app.data.repository.room.RoomSourceRepository
import com.cyberdoc.app.data.repository.room.RoomSyncRepository
import com.cyberdoc.app.domain.repository.DashboardRepository
import com.cyberdoc.app.domain.repository.DailyAggregateRepository
import com.cyberdoc.app.domain.repository.GoalRepository
import com.cyberdoc.app.domain.repository.MetricRepository
import com.cyberdoc.app.domain.repository.SourceRepository
import com.cyberdoc.app.domain.repository.SyncRepository
import com.cyberdoc.app.domain.service.DailyAggregateCalculator
import com.cyberdoc.app.domain.service.DefaultMetricsNormalizer
import com.cyberdoc.app.domain.usecase.BootstrapMvpDataUseCase
import com.cyberdoc.app.domain.usecase.GetDashboardSnapshotUseCase
import com.cyberdoc.app.domain.usecase.RegisterManualMetricUseCase
import com.cyberdoc.app.domain.usecase.SyncHealthConnectDataUseCase
import com.cyberdoc.app.domain.usecase.TriggerSyncUseCase
import com.cyberdoc.app.domain.usecase.UpsertGoalUseCase
import com.cyberdoc.app.integration.healthconnect.AndroidHealthConnectRepository
import com.cyberdoc.app.integration.healthconnect.HealthConnectRepository

class DefaultAppContainer(
    context: Context,
) : AppContainer {
    private val appContext = context.applicationContext
    private val clock = SystemTimeProvider
    private val database = CyberDocDatabase.create(appContext)

    private val sourceDao = database.dataSourceDao()
    private val metricDao = database.metricRecordDao()
    private val aggregateDao = database.dailyAggregateDao()
    private val goalDao = database.goalDao()
    private val syncRunDao = database.syncRunDao()

    private val normalizer = DefaultMetricsNormalizer()
    private val aggregateCalculator = DailyAggregateCalculator()

    override val dashboardRepository: DashboardRepository =
        RoomDashboardRepository(
            metricDao = metricDao,
            goalDao = goalDao,
            sourceDao = sourceDao,
            timeProvider = clock,
        )

    override val dailyAggregateRepository: DailyAggregateRepository =
        RoomDailyAggregateRepository(aggregateDao)

    override val goalRepository: GoalRepository =
        RoomGoalRepository(goalDao)

    override val metricRepository: MetricRepository =
        RoomMetricRepository(metricDao)

    override val sourceRepository: SourceRepository =
        RoomSourceRepository(sourceDao)

    override val syncRepository: SyncRepository =
        RoomSyncRepository(syncRunDao)

    override val healthConnectRepository: HealthConnectRepository =
        AndroidHealthConnectRepository(context = appContext)

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
    override val syncHealthConnectDataUseCase = SyncHealthConnectDataUseCase(
        healthConnectRepository = healthConnectRepository,
        sourceRepository = sourceRepository,
        metricRepository = metricRepository,
        dailyAggregateRepository = dailyAggregateRepository,
        syncRepository = syncRepository,
        normalizer = normalizer,
        aggregateCalculator = aggregateCalculator,
        timeProvider = clock,
    )
}
