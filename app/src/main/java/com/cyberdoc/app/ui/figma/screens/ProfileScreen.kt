package com.cyberdoc.app.ui.figma.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cyberdoc.app.ui.figma.components.SettingButton
import com.cyberdoc.app.ui.figma.components.SettingSwitch
import com.cyberdoc.app.ui.figma.components.StatValueCard

@Composable
fun ProfileScreen(
    onOpenGoals: () -> Unit,
    onOpenHealthConnect: () -> Unit,
) {
    var notifications by remember { mutableStateOf(true) }
    var darkMode by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Profile", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface) {
            Row(
                modifier = Modifier.fillMaxSize().padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary) {
                    Box(modifier = Modifier.size(54.dp), contentAlignment = Alignment.Center) {
                        Text("JD", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("John Doe", fontWeight = FontWeight.SemiBold)
                    Text("Local account", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatValueCard(label = "Avg Score", value = "87", unit = "", modifier = Modifier.weight(1f))
            StatValueCard(label = "Days Active", value = "23", unit = "", modifier = Modifier.weight(1f))
            StatValueCard(label = "Goals Met", value = "12", unit = "", modifier = Modifier.weight(1f))
        }

        SettingButton("Health Connect", "5 data sources connected", onClick = onOpenHealthConnect)
        SettingButton("Goals and Targets", "Manage your health goals", onClick = onOpenGoals)
        SettingSwitch("Notifications", "Daily reminders and alerts", notifications) { notifications = it }
        SettingSwitch("Dark Mode", "Switch to dark theme", darkMode) { darkMode = it }

        Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)) {
            Text(
                text = "100% private and local. All your health data stays on this device.",
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
