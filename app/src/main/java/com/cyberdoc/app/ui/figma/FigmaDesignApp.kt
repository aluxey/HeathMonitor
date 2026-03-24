package com.cyberdoc.app.ui.figma

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.cyberdoc.app.app.di.AppGraph
import com.cyberdoc.app.core.AppResult
import com.cyberdoc.app.domain.model.Goal
import com.cyberdoc.app.domain.model.MetricRecord
import com.cyberdoc.app.domain.model.PeriodType
import com.cyberdoc.app.domain.model.SourceStatus
import com.cyberdoc.app.domain.model.SourceType
import com.cyberdoc.app.domain.model.SyncRun
import com.cyberdoc.app.ui.figma.components.BottomNav
import com.cyberdoc.app.ui.figma.model.GoalDraft
import com.cyberdoc.app.ui.figma.model.GoalUi
import com.cyberdoc.app.ui.figma.model.ManualEntryDraft
import com.cyberdoc.app.ui.figma.model.dashboardSnapshotToMetrics
import com.cyberdoc.app.ui.figma.model.goalProgressToUi
import com.cyberdoc.app.ui.figma.model.metricInputToRawValue
import com.cyberdoc.app.ui.figma.model.metricStorageUnit
import com.cyberdoc.app.ui.figma.model.metricsData
import com.cyberdoc.app.ui.figma.navigation.AppTab
import com.cyberdoc.app.ui.figma.navigation.Overlay
import com.cyberdoc.app.ui.figma.navigation.RootStage
import com.cyberdoc.app.ui.figma.screens.DailySummaryScreen
import com.cyberdoc.app.ui.figma.screens.GoalsScreen
import com.cyberdoc.app.ui.figma.screens.HealthConnectScreen
import com.cyberdoc.app.ui.figma.screens.HomeScreen
import com.cyberdoc.app.ui.figma.screens.ManualEntryScreen
import com.cyberdoc.app.ui.figma.screens.MetricDetailScreen
import com.cyberdoc.app.ui.figma.screens.OnboardingScreen
import com.cyberdoc.app.ui.figma.screens.ProfileScreen
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.launch

private data class ActionUiState(
    val isSaving: Boolean = false,
    val message: String? = null,
    val error: String? = null,
)

@Composable
fun FigmaDesignApp() {
    val context = LocalContext.current
    var rootStage by remember { mutableStateOf<RootStage?>(null) }
    var appTab by remember { mutableStateOf(AppTab.HOME) }
    var overlay by remember { mutableStateOf(Overlay.NONE) }
    var selectedMetricId by remember { mutableStateOf("steps") }
    var metrics by remember { mutableStateOf(metricsData()) }
    var goals by remember { mutableStateOf<List<GoalUi>>(emptyList()) }
    var lastSyncLabel by remember { mutableStateOf<String?>(null) }
    var sourceCount by remember { mutableStateOf(0) }
    var connectedSourceCount by remember { mutableStateOf(0) }
    var trackedMetricCount by remember { mutableStateOf(0) }
    var manualEntryState by remember { mutableStateOf(ActionUiState()) }
    var goalState by remember { mutableStateOf(ActionUiState()) }
    var initialLoadCompleted by remember { mutableStateOf(false) }
    var syncInProgress by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val container = remember { AppGraph.container() }
    val sessionStore = remember { FigmaSessionStore(context) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val todayLabel = remember {
        LocalDate.now().format(
            DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault()),
        )
    }

    suspend fun refreshAppState(syncHealthData: Boolean) {
        container.bootstrapMvpDataUseCase()
        if (syncHealthData) {
            if (syncInProgress) return
            syncInProgress = true
        }

        try {
            if (syncHealthData) {
                container.syncHealthConnectDataUseCase(daysBack = 30)
            }

            val snapshot = runCatching { container.getDashboardSnapshotUseCase() }.getOrNull()
            metrics = dashboardSnapshotToMetrics(snapshot)
            goals = runCatching { goalProgressToUi(container.getGoalProgressUseCase()) }.getOrDefault(emptyList())

            val sources = snapshot?.sources ?: runCatching { container.sourceRepository.all() }.getOrDefault(emptyList())
            val healthSources = sources.filter { it.type == SourceType.HEALTH_CONNECT }
            sourceCount = healthSources.size
            connectedSourceCount = healthSources.count { it.status == SourceStatus.CONNECTED }
            trackedMetricCount = snapshot?.metrics?.size ?: 0
            lastSyncLabel = runCatching {
                formatSyncSummary(container.syncRepository.latest(limit = 1).firstOrNull())
            }.getOrNull()
        } finally {
            if (syncHealthData) {
                syncInProgress = false
            }
        }
    }

    suspend fun goToBestNextStageAfterOnboarding() {
        val granted = runCatching { container.healthConnectRepository.grantedDataTypes() }.getOrDefault(emptySet())
        rootStage = if (granted.isNotEmpty()) RootStage.APP else RootStage.HEALTH_CONNECT
        if (rootStage == RootStage.APP) {
            refreshAppState(syncHealthData = true)
        }
        initialLoadCompleted = true
    }

    suspend fun saveManualEntry(entry: ManualEntryDraft) {
        manualEntryState = ActionUiState(isSaving = true)
        val now = Instant.now()
        val result = container.registerManualMetricUseCase(
            MetricRecord(
                id = UUID.randomUUID().toString(),
                metricType = entry.metricType,
                value = metricInputToRawValue(entry.metricType, entry.value),
                unit = metricStorageUnit(entry.metricType),
                startAt = now,
                endAt = now,
                sourceId = "manual",
                externalId = null,
                isManual = true,
                createdAt = now,
            ),
        )

        when (result) {
            is AppResult.Success -> {
                refreshAppState(syncHealthData = false)
                manualEntryState = ActionUiState(message = "Entry saved successfully")
            }

            is AppResult.Failure -> {
                manualEntryState = ActionUiState(error = result.error.message)
            }
        }
    }

    suspend fun saveGoal(goalDraft: GoalDraft) {
        goalState = ActionUiState(isSaving = true)
        val existingGoal = goals.firstOrNull { it.metricType == goalDraft.metricType }
        val result = container.upsertGoalUseCase(
            Goal(
                id = existingGoal?.id ?: "goal_${goalDraft.metricType.name.lowercase(Locale.US)}",
                metricType = goalDraft.metricType,
                targetValue = metricInputToRawValue(goalDraft.metricType, goalDraft.targetValue),
                periodType = PeriodType.DAILY,
                startDate = LocalDate.now(),
                endDate = null,
                isActive = true,
            ),
        )

        when (result) {
            is AppResult.Success -> {
                refreshAppState(syncHealthData = false)
                goalState = ActionUiState(message = "Goal saved successfully")
            }

            is AppResult.Failure -> {
                goalState = ActionUiState(error = result.error.message)
            }
        }
    }

    LaunchedEffect(Unit) {
        val onboardingDone = sessionStore.isOnboardingDone()
        val grantedAtBoot = runCatching { container.healthConnectRepository.grantedDataTypes() }.getOrDefault(emptySet())

        if (!onboardingDone && grantedAtBoot.isNotEmpty()) {
            sessionStore.setOnboardingDone(true)
            rootStage = RootStage.APP
            refreshAppState(syncHealthData = true)
            return@LaunchedEffect
        }

        if (!onboardingDone) {
            rootStage = RootStage.ONBOARDING
            return@LaunchedEffect
        }

        rootStage = if (grantedAtBoot.isNotEmpty()) RootStage.APP else RootStage.HEALTH_CONNECT
        if (rootStage == RootStage.APP) {
            refreshAppState(syncHealthData = true)
        } else {
            refreshAppState(syncHealthData = false)
        }
        initialLoadCompleted = true
    }

    DisposableEffect(lifecycleOwner, rootStage, initialLoadCompleted) {
        val observer = LifecycleEventObserver { _, event ->
            if (
                event == Lifecycle.Event.ON_RESUME &&
                initialLoadCompleted &&
                rootStage == RootStage.APP
            ) {
                scope.launch {
                    refreshAppState(syncHealthData = true)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 470.dp),
            ) {
                when (rootStage) {
                    null -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Loading...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    RootStage.ONBOARDING -> OnboardingScreen(
                        onNext = { done ->
                            if (done) {
                                sessionStore.setOnboardingDone(true)
                                scope.launch { goToBestNextStageAfterOnboarding() }
                            }
                        },
                        onSkip = {
                            sessionStore.setOnboardingDone(true)
                            scope.launch { goToBestNextStageAfterOnboarding() }
                        },
                    )

                    RootStage.HEALTH_CONNECT -> HealthConnectScreen(
                        onContinue = {
                            scope.launch {
                                refreshAppState(syncHealthData = true)
                                initialLoadCompleted = true
                                rootStage = RootStage.APP
                            }
                        },
                        onSkip = {
                            scope.launch {
                                refreshAppState(syncHealthData = false)
                                initialLoadCompleted = true
                                rootStage = RootStage.APP
                            }
                        },
                    )

                    RootStage.APP -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Box(modifier = Modifier.weight(1f)) {
                                when (overlay) {
                                    Overlay.NONE -> when (appTab) {
                                        AppTab.HOME -> HomeScreen(
                                            metrics = metrics,
                                            todayLabel = todayLabel,
                                            lastSyncLabel = lastSyncLabel,
                                            onOpenSummary = { overlay = Overlay.SUMMARY },
                                            onOpenManual = {
                                                manualEntryState = ActionUiState()
                                                overlay = Overlay.MANUAL
                                            },
                                            onOpenGoals = { appTab = AppTab.GOALS },
                                            onOpenHealthConnect = { rootStage = RootStage.HEALTH_CONNECT },
                                            onOpenMetric = {
                                                selectedMetricId = it
                                                overlay = Overlay.METRIC
                                            },
                                        )

                                        AppTab.GOALS -> GoalsScreen(
                                            goals = goals,
                                            isSaving = goalState.isSaving,
                                            feedbackMessage = goalState.message,
                                            errorMessage = goalState.error,
                                            onSaveGoal = { goalDraft ->
                                                scope.launch { saveGoal(goalDraft) }
                                            },
                                        )

                                        AppTab.PROFILE -> ProfileScreen(
                                            connectedSourceCount = connectedSourceCount,
                                            sourceCount = sourceCount,
                                            trackedMetricCount = trackedMetricCount,
                                            goalCount = goals.size,
                                            lastSyncLabel = lastSyncLabel,
                                            onOpenGoals = { appTab = AppTab.GOALS },
                                            onOpenHealthConnect = { rootStage = RootStage.HEALTH_CONNECT },
                                        )
                                    }

                                    Overlay.SUMMARY -> DailySummaryScreen(
                                        metrics = metrics,
                                        goals = goals,
                                        lastSyncLabel = lastSyncLabel,
                                        onBack = { overlay = Overlay.NONE },
                                    )
                                    Overlay.MANUAL -> ManualEntryScreen(
                                        isSaving = manualEntryState.isSaving,
                                        saveMessage = manualEntryState.message,
                                        saveError = manualEntryState.error,
                                        onSave = { draft ->
                                            scope.launch { saveManualEntry(draft) }
                                        },
                                        onBack = {
                                            manualEntryState = ActionUiState()
                                            overlay = Overlay.NONE
                                        },
                                    )

                                    Overlay.METRIC -> MetricDetailScreen(
                                        metric = metrics.firstOrNull { it.id == selectedMetricId }
                                            ?: metrics.firstOrNull()
                                            ?: metricsData().first(),
                                        onBack = { overlay = Overlay.NONE },
                                    )
                                }
                            }

                            if (overlay != Overlay.MANUAL) {
                                BottomNav(
                                    tab = if (overlay == Overlay.NONE) appTab else null,
                                    onTab = {
                                        overlay = Overlay.NONE
                                        appTab = it
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatSyncSummary(run: SyncRun?): String? {
    if (run == null) return null

    val localTime = run.endedAt.atZone(ZoneId.systemDefault())
    val timeLabel = localTime.format(DateTimeFormatter.ofPattern("MMM d, HH:mm", Locale.getDefault()))
    return "${run.status.name.lowercase(Locale.US).replaceFirstChar { it.titlecase(Locale.US) }} on $timeLabel"
}
