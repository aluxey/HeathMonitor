package com.cyberdoc.app.ui.sources

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.PermissionController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cyberdoc.app.domain.model.HealthConnectAvailability
import com.cyberdoc.app.domain.model.SourceStatus
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

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Health Connect",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = availabilityLabel(uiState.availability),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Permissions ${uiState.grantedPermissionsCount}/${uiState.requiredPermissionsCount}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    if (uiState.availability == HealthConnectAvailability.AVAILABLE) {
                        Button(
                            onClick = { permissionLauncher.launch(requiredPermissions) },
                        ) {
                            Text(
                                if (uiState.allPermissionsGranted) {
                                    "Verifier les permissions"
                                } else {
                                    "Accorder les permissions"
                                },
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { viewModel.syncNow() },
                            enabled = uiState.allPermissionsGranted && !uiState.isSyncing,
                        ) {
                            Text(
                                if (uiState.isSyncing) {
                                    "Synchronisation..."
                                } else {
                                    "Synchroniser maintenant"
                                },
                            )
                        }
                        if (!uiState.allPermissionsGranted) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "La synchro est activee quand toutes les permissions sont accordees.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    uiState.lastSyncMessage?.let { message ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = syncStatusLabel(uiState.lastSyncStatus),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        uiState.lastSyncRecordsRead?.let { records ->
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Enregistrements importes: $records",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }

        items(uiState.sources) { source ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = source.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Statut ${statusLabel(source.status)}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Priorite ${source.priority}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Derniere synchro ${source.lastSyncLabel}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    source.lastError?.let { error ->
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
        }
    }
}

private fun availabilityLabel(availability: HealthConnectAvailability): String = when (availability) {
    HealthConnectAvailability.AVAILABLE -> "Disponible sur cet appareil"
    HealthConnectAvailability.NOT_INSTALLED -> "Installe ou mets a jour Health Connect"
    HealthConnectAvailability.NOT_SUPPORTED -> "Non supporte sur cet appareil"
}

private fun statusLabel(status: SourceStatus): String = when (status) {
    SourceStatus.CONNECTED -> "Connectee"
    SourceStatus.NEEDS_PERMISSION -> "Permissions requises"
    SourceStatus.UNAVAILABLE -> "Indisponible"
    SourceStatus.ERROR -> "Erreur"
}

private fun syncStatusLabel(status: SyncStatus?): String = when (status) {
    SyncStatus.SUCCESS -> "Synchro terminee"
    SyncStatus.PARTIAL_SUCCESS -> "Synchro partielle"
    SyncStatus.FAILURE -> "Synchro en echec"
    SyncStatus.IDLE -> "Synchro inactive"
    null -> "Etat synchro inconnu"
}
