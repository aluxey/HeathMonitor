package com.cyberdoc.app.ui.sources

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.PermissionController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cyberdoc.app.domain.model.HealthConnectAvailability
import com.cyberdoc.app.domain.model.SourceStatus
import com.cyberdoc.app.domain.model.SourceStatusItem
import com.cyberdoc.app.domain.model.SyncStatus

@Composable
fun SourcesScreen(
    viewModel: SourcesViewModel,
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val requiredPermissions = remember(viewModel) { viewModel.requiredPermissions() }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract(),
    ) { _ ->
        viewModel.refreshPermissions()
    }
    val progress = if (uiState.requiredPermissionsCount == 0) {
        0f
    } else {
        uiState.grantedPermissionsCount.toFloat() / uiState.requiredPermissionsCount.toFloat()
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            HealthConnectHero(
                uiState = uiState,
                progress = progress,
                onRequestPermissions = { permissionLauncher.launch(requiredPermissions) },
                onSync = viewModel::syncNow,
            )
        }

        item {
            Text(
                text = "Sources disponibles",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }

        items(uiState.sources) { source ->
            SourceCard(source = source)
        }
    }
}

@Composable
private fun HealthConnectHero(
    uiState: SourcesUiState,
    progress: Float,
    onRequestPermissions: () -> Unit,
    onSync: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Health Connect",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = availabilityLabel(uiState.availability),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                StatusBadge(status = healthConnectStatus(uiState, MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.onSurfaceVariant))
            }

            Surface(shape = RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = "Permissions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "${uiState.grantedPermissionsCount}/${uiState.requiredPermissionsCount} accordees",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    LinearProgressIndicator(
                        progress = { progress.coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.secondaryContainer,
                    )
                    Text(
                        text = when {
                            uiState.availability != HealthConnectAvailability.AVAILABLE -> "Health Connect doit etre disponible avant la connexion."
                            uiState.allPermissionsGranted -> "Tout est pret pour une synchro complete."
                            uiState.grantedPermissionsCount == 0 -> "Commence par autoriser l'acces aux donnees essentielles."
                            else -> "Connexion partielle: certaines cartes resteront incompletes."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onRequestPermissions,
                    enabled = uiState.availability == HealthConnectAvailability.AVAILABLE,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(if (uiState.allPermissionsGranted) "Verifier" else "Permissions")
                }
                Button(
                    onClick = onSync,
                    enabled = uiState.allPermissionsGranted && !uiState.isSyncing,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(if (uiState.isSyncing) "Synchro..." else "Synchroniser")
                }
            }

            uiState.lastSyncStatus?.let { status ->
                Surface(shape = RoundedCornerShape(24.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = syncStatusLabel(status),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        uiState.lastSyncMessage?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        uiState.lastSyncRecordsRead?.let {
                            Text(
                                text = "Elements lus: $it",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SourceCard(source: SourceStatusItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = source.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "Priorite ${source.priority}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                StatusBadge(status = sourceStatusBadge(source.status))
            }
            Text(
                text = "Derniere synchro ${source.lastSyncLabel}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            source.lastError?.let { error ->
                Surface(shape = RoundedCornerShape(18.dp), color = MaterialTheme.colorScheme.error.copy(alpha = 0.10f)) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: StatusBadgeUi) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = status.color.copy(alpha = 0.14f),
    ) {
        Text(
            text = status.label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge,
            color = status.color,
        )
    }
}

private fun healthConnectStatus(
    uiState: SourcesUiState,
    primary: Color,
    secondary: Color,
    error: Color,
    muted: Color,
): StatusBadgeUi = when {
    uiState.availability != HealthConnectAvailability.AVAILABLE -> StatusBadgeUi("Indisponible", muted)
    uiState.allPermissionsGranted && uiState.lastSyncStatus == SyncStatus.SUCCESS -> StatusBadgeUi("Connecte", primary)
    uiState.allPermissionsGranted -> StatusBadgeUi("Pret", secondary)
    uiState.grantedPermissionsCount > 0 -> StatusBadgeUi("Partiel", secondary)
    else -> StatusBadgeUi("A configurer", error)
}

private fun sourceStatusBadge(status: SourceStatus): StatusBadgeUi = when (status) {
    SourceStatus.CONNECTED -> StatusBadgeUi("Connectee", Color(0xFF4E7A57))
    SourceStatus.NEEDS_PERMISSION -> StatusBadgeUi("Permissions", Color(0xFFAF7C43))
    SourceStatus.UNAVAILABLE -> StatusBadgeUi("Indisponible", Color(0xFF6C766F))
    SourceStatus.ERROR -> StatusBadgeUi("Erreur", Color(0xFFB4675D))
}

private fun availabilityLabel(availability: HealthConnectAvailability): String = when (availability) {
    HealthConnectAvailability.AVAILABLE -> "Disponible sur cet appareil et pret pour une connexion wellness-tech rassurante."
    HealthConnectAvailability.NOT_INSTALLED -> "Installe ou mets a jour Health Connect pour activer la synchro."
    HealthConnectAvailability.NOT_SUPPORTED -> "Cet appareil ne prend pas en charge Health Connect."
}

private fun syncStatusLabel(status: SyncStatus): String = when (status) {
    SyncStatus.SUCCESS -> "Synchronisation terminee"
    SyncStatus.PARTIAL_SUCCESS -> "Synchronisation partielle"
    SyncStatus.FAILURE -> "Synchronisation en echec"
    SyncStatus.IDLE -> "Synchronisation inactive"
}

private data class StatusBadgeUi(
    val label: String,
    val color: Color,
)
