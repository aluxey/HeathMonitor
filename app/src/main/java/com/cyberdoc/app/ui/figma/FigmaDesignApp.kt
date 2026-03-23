package com.cyberdoc.app.ui.figma

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cyberdoc.app.ui.figma.components.BottomNav
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

@Composable
fun FigmaDesignApp() {
    var rootStage by remember { mutableStateOf(RootStage.ONBOARDING) }
    var appTab by remember { mutableStateOf(AppTab.HOME) }
    var overlay by remember { mutableStateOf(Overlay.NONE) }
    var selectedMetricId by remember { mutableStateOf("steps") }

    val metrics = remember { metricsData() }

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
                    RootStage.ONBOARDING -> OnboardingScreen(
                        onNext = { done ->
                            if (done) rootStage = RootStage.HEALTH_CONNECT
                        },
                        onSkip = { rootStage = RootStage.HEALTH_CONNECT },
                    )

                    RootStage.HEALTH_CONNECT -> HealthConnectScreen(
                        onContinue = { rootStage = RootStage.APP },
                        onSkip = { rootStage = RootStage.APP },
                    )

                    RootStage.APP -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Box(modifier = Modifier.weight(1f)) {
                                when (overlay) {
                                    Overlay.NONE -> when (appTab) {
                                        AppTab.HOME -> HomeScreen(
                                            metrics = metrics,
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
                                        metric = metrics.first { it.id == selectedMetricId },
                                        onBack = { overlay = Overlay.NONE },
                                    )
                                }
                            }

                            if (overlay == Overlay.NONE) {
                                BottomNav(tab = appTab, onTab = { appTab = it })
                            }
                        }
                    }
                }
            }
        }
    }
}
