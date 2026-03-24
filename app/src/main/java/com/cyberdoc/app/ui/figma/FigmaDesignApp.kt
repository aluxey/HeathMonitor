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
import com.cyberdoc.app.app.di.AppGraph
import com.cyberdoc.app.ui.figma.components.BottomNav
import com.cyberdoc.app.ui.figma.model.dashboardSnapshotToMetrics
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
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun FigmaDesignApp() {
    val context = LocalContext.current
    var rootStage by remember { mutableStateOf<RootStage?>(null) }
    var appTab by remember { mutableStateOf(AppTab.HOME) }
    var overlay by remember { mutableStateOf(Overlay.NONE) }
    var selectedMetricId by remember { mutableStateOf("steps") }
    var metrics by remember { mutableStateOf(metricsData()) }
    val scope = rememberCoroutineScope()
    val container = remember { AppGraph.container() }
    val sessionStore = remember { FigmaSessionStore(context) }
    val todayLabel = remember {
        LocalDate.now().format(
            DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault()),
        )
    }

    suspend fun refreshDashboard() {
        container.syncHealthConnectDataUseCase(daysBack = 7)
        val snapshot = runCatching { container.getDashboardSnapshotUseCase() }.getOrNull()
        metrics = dashboardSnapshotToMetrics(snapshot)
    }

    suspend fun goToBestNextStageAfterOnboarding() {
        val granted = runCatching { container.healthConnectRepository.grantedDataTypes() }.getOrDefault(emptySet())
        rootStage = if (granted.isNotEmpty()) RootStage.APP else RootStage.HEALTH_CONNECT
        if (rootStage == RootStage.APP) {
            refreshDashboard()
        }
    }

    LaunchedEffect(Unit) {
        val onboardingDone = sessionStore.isOnboardingDone()
        val grantedAtBoot = runCatching { container.healthConnectRepository.grantedDataTypes() }.getOrDefault(emptySet())

        if (!onboardingDone && grantedAtBoot.isNotEmpty()) {
            sessionStore.setOnboardingDone(true)
            rootStage = RootStage.APP
            refreshDashboard()
            return@LaunchedEffect
        }

        if (!onboardingDone) {
            rootStage = RootStage.ONBOARDING
            return@LaunchedEffect
        }

        rootStage = if (grantedAtBoot.isNotEmpty()) RootStage.APP else RootStage.HEALTH_CONNECT
        if (rootStage == RootStage.APP) {
            refreshDashboard()
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
                                refreshDashboard()
                                rootStage = RootStage.APP
                            }
                        },
                        onSkip = {
                            scope.launch {
                                refreshDashboard()
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
                                            onOpenSummary = { overlay = Overlay.SUMMARY },
                                            onOpenManual = { overlay = Overlay.MANUAL },
                                            onOpenGoals = { appTab = AppTab.GOALS },
                                            onOpenHealthConnect = { rootStage = RootStage.HEALTH_CONNECT },
                                            onOpenMetric = {
                                                selectedMetricId = it
                                                overlay = Overlay.METRIC
                                            },
                                        )

                                        AppTab.GOALS -> GoalsScreen()
                                        AppTab.PROFILE -> ProfileScreen(
                                            onOpenGoals = { appTab = AppTab.GOALS },
                                            onOpenHealthConnect = { rootStage = RootStage.HEALTH_CONNECT },
                                        )
                                    }

                                    Overlay.SUMMARY -> DailySummaryScreen(onBack = { overlay = Overlay.NONE })
                                    Overlay.MANUAL -> ManualEntryScreen(onBack = { overlay = Overlay.NONE })
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
