package com.cyberdoc.app.ui.figma.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.cyberdoc.app.app.di.AppGraph
import com.cyberdoc.app.integration.healthconnect.HealthConnectAvailability
import com.cyberdoc.app.integration.healthconnect.HealthConnectPermissions
import com.cyberdoc.app.integration.healthconnect.HealthDataType
import com.cyberdoc.app.ui.figma.components.metricIcon
import kotlinx.coroutines.launch

private data class PermissionOption(
    val type: HealthDataType,
    val label: String,
    val description: String,
)

@Composable
fun HealthConnectScreen(
    onContinue: () -> Unit,
    onSkip: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val container = remember { AppGraph.container() }
    val repository = remember { container.healthConnectRepository }

    val options = remember {
        listOf(
            PermissionOption(HealthDataType.STEPS, "Steps", "Daily step count"),
            PermissionOption(HealthDataType.HEART_RATE, "Heart Rate", "Heart rate measurements"),
            PermissionOption(HealthDataType.SLEEP, "Sleep", "Sleep duration and sessions"),
            PermissionOption(HealthDataType.WEIGHT, "Weight", "Body weight measurements"),
            PermissionOption(HealthDataType.HYDRATION, "Hydration", "Water intake"),
            PermissionOption(HealthDataType.CALORIES_IN, "Nutrition", "Calories and food intake"),
        )
    }

    val selected = remember { mutableStateListOf<HealthDataType>() }
    var granted by remember { mutableStateOf(setOf<HealthDataType>()) }
    var availability by remember { mutableStateOf(HealthConnectAvailability.NOT_SUPPORTED) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }
    var lastRequestedCount by remember { mutableStateOf(0) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract(),
    ) { grantedPermissions ->
        val newlyGranted = grantedPermissions.mapNotNullTo(mutableSetOf()) {
            HealthConnectPermissions.toHealthDataType(it)
        }
        scope.launch {
            loading = true
            availability = repository.availability()
            granted = repository.grantedDataTypes()
            statusMessage = when {
                granted.isEmpty() -> "No permission granted. Please allow at least one metric."
                granted.size < options.size -> "Granted ${granted.size}/${options.size} permissions."
                else -> "All permissions granted."
            }
            loading = false
            if (newlyGranted.isNotEmpty()) {
                onContinue()
            }
        }
    }

    fun refreshStatus() {
        scope.launch {
            loading = true
            availability = repository.availability()
            granted = if (availability == HealthConnectAvailability.AVAILABLE) {
                repository.grantedDataTypes()
            } else {
                emptySet()
            }
            if (selected.isEmpty()) {
                selected.addAll(
                    listOf(
                        HealthDataType.STEPS,
                        HealthDataType.HEART_RATE,
                        HealthDataType.SLEEP,
                    ),
                )
            }
            statusMessage = when {
                availability != HealthConnectAvailability.AVAILABLE ->
                    "Health Connect unavailable: $availability"

                granted.isEmpty() -> "No permission granted yet. Tap Connect to continue."
                else -> null
            }
            loading = false
        }
    }

    LaunchedEffect(Unit) {
        refreshStatus()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshStatus()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val selectedPendingPermissions = selected
        .filterNot { granted.contains(it) }
        .map { HealthConnectPermissions.toPermission(it) }
        .toSet()
    val canRequestPermissions =
        availability == HealthConnectAvailability.AVAILABLE && selected.isNotEmpty()

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Text(
                text = "Skip for now",
                modifier = Modifier.clickable(onClick = onSkip),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentAlignment = Alignment.Center,
            ) {
                Surface(
                    shape = RoundedCornerShape(30.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                ) {
                    Box(
                        modifier = Modifier.padding(28.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CloudDownload,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(62.dp),
                        )
                    }
                }
            }

            Text(
                text = "Connect Health Data",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Sync your health data from Health Connect to automatically track your wellness metrics.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "Local & Secure",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Data syncs directly from Health Connect to this app only. Nothing is uploaded to external servers.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = "Status: $availability • ${granted.size}/${options.size} granted",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                        )
                        statusMessage?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Select data to sync",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            items(options) { permission ->
                val checked = selected.contains(permission.type)
                val isGranted = granted.contains(permission.type)

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (checked) selected.remove(permission.type) else selected.add(permission.type)
                        },
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(
                        width = if (checked) 1.5.dp else 1.dp,
                        color = if (checked) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)
                        },
                    ),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Checkbox(checked = checked, onCheckedChange = null)
                        PermissionIcon(type = permission.type)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = permission.label,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = permission.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Text(
                            text = if (isGranted) "Granted" else "Pending",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isGranted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Button(
                onClick = {
                    if (selectedPendingPermissions.isEmpty()) {
                        onContinue()
                    } else {
                        lastRequestedCount = selectedPendingPermissions.size
                        permissionLauncher.launch(selectedPendingPermissions)
                    }
                },
                enabled = canRequestPermissions && !loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
            ) {
                Text(
                    text = when {
                        loading -> "Loading status..."
                        selectedPendingPermissions.isEmpty() -> "Continue"
                        else -> "Connect ${selectedPendingPermissions.size} data sources"
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            if (availability == HealthConnectAvailability.AVAILABLE && granted.isEmpty()) {
                Button(
                    onClick = {
                        val intent = HealthConnectClient.getHealthConnectManageDataIntent(context)
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(18.dp),
                ) {
                    Text("Open Health Connect settings")
                }
            }

            Text(
                text = if (granted.isEmpty() && lastRequestedCount > 0) {
                    "If permissions stay pending, grant them manually in Health Connect settings."
                } else {
                    "You can change these permissions anytime in Settings."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun PermissionIcon(type: HealthDataType) {
    val iconName = when (type) {
        HealthDataType.STEPS -> "steps"
        HealthDataType.HEART_RATE -> "heart"
        HealthDataType.SLEEP -> "sleep"
        HealthDataType.WEIGHT -> "weight"
        HealthDataType.HYDRATION -> "hydration"
        HealthDataType.CALORIES_IN -> "calories"
    }
    val icon: ImageVector = metricIcon(iconName)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
    ) {
        Box(
            modifier = Modifier.padding(10.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}
