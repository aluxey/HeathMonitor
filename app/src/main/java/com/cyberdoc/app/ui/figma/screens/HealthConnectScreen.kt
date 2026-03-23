package com.cyberdoc.app.ui.figma.screens

import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.cyberdoc.app.app.di.AppGraph
import com.cyberdoc.app.integration.healthconnect.HealthConnectAvailability
import com.cyberdoc.app.integration.healthconnect.HealthConnectPermissions
import com.cyberdoc.app.integration.healthconnect.HealthDataType
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
            PermissionOption(HealthDataType.SLEEP, "Sleep", "Sleep duration and sessions"),
            PermissionOption(HealthDataType.WEIGHT, "Weight", "Body weight measurements"),
            PermissionOption(HealthDataType.HYDRATION, "Hydration", "Water intake"),
            PermissionOption(HealthDataType.CALORIES_IN, "Nutrition", "Calories and food intake"),
            PermissionOption(HealthDataType.HEART_RATE, "Heart Rate", "Heart rate measurements"),
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
                        HealthDataType.SLEEP,
                        HealthDataType.WEIGHT,
                    ),
                )
            }
            statusMessage = when {
                availability != HealthConnectAvailability.AVAILABLE ->
                    "Health Connect unavailable: $availability"

                granted.isEmpty() -> "No permission granted yet. Tap Grant permissions."
                else -> null
            }
            loading = false
        }
    }

    LaunchedEffect(Unit) {
        refreshStatus()
    }
    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
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
    val grantedCount = granted.size

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
            Text(
                text = "Skip for now",
                modifier = Modifier.clickable(onClick = onSkip),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(18.dp))
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "CONNECT",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Connect Health Data",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Authorize Health Connect data to populate your dashboard with real measurements.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Local and secure. Data is not uploaded to external servers.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Status: $availability - Granted $grantedCount / ${options.size}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        statusMessage?.let {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { refreshStatus() },
                            enabled = !loading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp),
                            shape = RoundedCornerShape(10.dp),
                        ) {
                            Text("Refresh status", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
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
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = if (checked) 1.dp else 0.dp,
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Checkbox(checked = checked, onCheckedChange = null)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                permission.label,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                permission.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
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
        }

        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
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
                shape = RoundedCornerShape(14.dp),
            ) {
                val label = when {
                    loading -> "Loading status..."
                    selectedPendingPermissions.isEmpty() -> "Continue"
                    else -> "Grant ${selectedPendingPermissions.size} permissions"
                }
                Text(label)
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (availability == HealthConnectAvailability.AVAILABLE && granted.isEmpty()) {
                Button(
                    onClick = {
                        val intent = HealthConnectClient.getHealthConnectManageDataIntent(context)
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Text("Open Health Connect settings")
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text(
                if (granted.isEmpty() && lastRequestedCount > 0) {
                    "If permissions stay pending, grant them manually in Health Connect settings."
                } else {
                    "You can update these permissions later in Health Connect settings."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
