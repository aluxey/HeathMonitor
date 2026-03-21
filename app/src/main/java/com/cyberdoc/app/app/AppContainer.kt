package com.cyberdoc.app.app

import android.content.Context
import com.cyberdoc.app.data.local.CyberDocDatabase
import com.cyberdoc.app.data.repository.DefaultDashboardRepository
import com.cyberdoc.app.data.repository.DefaultGoalRepository
import com.cyberdoc.app.data.repository.DefaultSourceRepository
import com.cyberdoc.app.domain.repository.DashboardRepository
import com.cyberdoc.app.domain.repository.GoalRepository
import com.cyberdoc.app.domain.repository.SourceRepository
import com.cyberdoc.app.domain.usecase.SaveManualWeightUseCase
import com.cyberdoc.app.domain.usecase.SeedDemoDataUseCase
import com.cyberdoc.app.domain.usecase.SyncHealthDataUseCase
import com.cyberdoc.app.integration.healthconnect.HealthConnectGateway
import com.cyberdoc.app.integration.healthconnect.HealthConnectManager
import com.cyberdoc.app.integration.healthconnect.RealHealthConnectManager

interface AppContainer {
    val dashboardRepository: DashboardRepository
    val goalRepository: GoalRepository
    val sourceRepository: SourceRepository
    val healthConnectManager: HealthConnectManager
    val seedDemoDataUseCase: SeedDemoDataUseCase
    val syncHealthDataUseCase: SyncHealthDataUseCase
    val saveManualWeightUseCase: SaveManualWeightUseCase
}

class DefaultAppContainer(context: Context) : AppContainer {
    private val appContext = context.applicationContext
    private val database = CyberDocDatabase.create(appContext)

    override val dashboardRepository: DashboardRepository =
        DefaultDashboardRepository(
            aggregateDao = database.dailyAggregateDao(),
            goalDao = database.goalDao(),
            sourceDao = database.dataSourceDao(),
        )

    override val goalRepository: GoalRepository =
        DefaultGoalRepository(database.goalDao())

    override val sourceRepository: SourceRepository =
        DefaultSourceRepository(
            sourceDao = database.dataSourceDao(),
            syncRunDao = database.syncRunDao(),
        )

    override val healthConnectManager: HealthConnectManager =
        RealHealthConnectManager(appContext)

    override val seedDemoDataUseCase = SeedDemoDataUseCase(database)

    override val syncHealthDataUseCase = SyncHealthDataUseCase(
        database = database,
        gateway = HealthConnectGateway(healthConnectManager),
    )

    override val saveManualWeightUseCase = SaveManualWeightUseCase(database)
}
