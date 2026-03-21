package com.cyberdoc.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cyberdoc.app.app.AppContainer
import com.cyberdoc.app.ui.dashboard.DashboardScreen
import com.cyberdoc.app.ui.dashboard.DashboardViewModel
import com.cyberdoc.app.ui.sources.SourcesScreen
import com.cyberdoc.app.ui.sources.SourcesViewModel

private enum class AppDestination(val label: String) {
    DASHBOARD("Aujourd'hui"),
    SOURCES("Sources"),
}

@Composable
fun CyberDocApp(
    container: AppContainer,
) {
    var destination by remember { mutableStateOf(AppDestination.DASHBOARD) }
    val dashboardViewModel: DashboardViewModel = viewModel(
        factory = cyberDocViewModelFactory {
            DashboardViewModel(
                dashboardRepository = container.dashboardRepository,
                saveManualWeightUseCase = container.saveManualWeightUseCase,
            )
        },
    )
    val sourcesViewModel: SourcesViewModel = viewModel(
        factory = cyberDocViewModelFactory {
            SourcesViewModel(
                sourceRepository = container.sourceRepository,
                healthConnectManager = container.healthConnectManager,
            )
        },
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f),
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.20f),
                        ),
                    ),
                )
                .padding(innerPadding),
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                ) {
                    Text(
                        text = "CyberDoc",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tableau de bord sante personnel local-first pour le MVP Android.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        AppDestination.entries.forEach { item ->
                            Button(
                                onClick = { destination = item },
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .width(160.dp),
                            ) {
                                Text(text = item.label, color = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
            ) {
                when (destination) {
                    AppDestination.DASHBOARD -> DashboardScreen(viewModel = dashboardViewModel)
                    AppDestination.SOURCES -> SourcesScreen(viewModel = sourcesViewModel)
                }
            }
        }
    }
}
