package com.cyberdoc.app.ui.figma.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cyberdoc.app.domain.model.MetricType
import com.cyberdoc.app.ui.figma.components.SettingRow
import com.cyberdoc.app.ui.figma.components.metricIcon
import com.cyberdoc.app.ui.figma.components.profileIcon
import com.cyberdoc.app.ui.figma.model.MetricSourceOptionUi
import com.cyberdoc.app.ui.figma.model.MetricSourceSettingUi

@Composable
fun ProfileScreen(
    connectedSourceCount: Int,
    sourceCount: Int,
    trackedMetricCount: Int,
    goalCount: Int,
    lastSyncLabel: String?,
    metricSourceSettings: List<MetricSourceSettingUi>,
    onOpenGoals: () -> Unit,
    onOpenHealthConnect: () -> Unit,
    onSelectMetricSource: (MetricType, String?) -> Unit,
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }
    var selectedMetricSetting by remember { mutableStateOf<MetricSourceSettingUi?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
            ) {
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)) {
                    Text(
                        text = "Profil",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )

                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                        ) {
                            Box(
                                modifier = Modifier.size(64.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "ME",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Cet appareil",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "Profil local",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }

        item {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 18.dp),
                    ) {
                        SummaryStat(label = "Sources", value = "$connectedSourceCount", modifier = Modifier.weight(1f))
                        SummaryStat(label = "Suivies", value = "$trackedMetricCount", modifier = Modifier.weight(1f))
                        SummaryStat(label = "Objectifs", value = "$goalCount", modifier = Modifier.weight(1f))
                    }
                }

                SettingSection(title = "Donnees sante") {
                    SettingRow(
                        icon = profileIcon("health"),
                        title = "Health Connect",
                        subtitle = if (sourceCount == 0) {
                            "Pas encore connecte"
                        } else {
                            "$connectedSourceCount sources connectees sur $sourceCount"
                        },
                        onClick = onOpenHealthConnect,
                    )
                    SettingDivider()
                    SettingRow(
                        icon = profileIcon("goals"),
                        title = "Objectifs",
                        subtitle = "$goalCount objectifs configures",
                        onClick = onOpenGoals,
                    )
                    SettingDivider()
                    SettingRow(
                        icon = profileIcon("storage"),
                        title = "Stockage des donnees",
                        subtitle = lastSyncLabel ?: "Toutes les donnees sont stockees localement",
                        onClick = {},
                    )
                }

                SettingSection(title = "Sources par metrique") {
                    metricSourceSettings.forEachIndexed { index, setting ->
                        SettingRow(
                            icon = metricIcon(setting.metricId),
                            title = setting.title,
                            subtitle = setting.summary,
                            onClick = if (setting.options.size > 1) {
                                { selectedMetricSetting = setting }
                            } else {
                                null
                            },
                        )
                        if (index < metricSourceSettings.lastIndex) {
                            SettingDivider()
                        }
                    }
                }

                SettingSection(title = "Preferences") {
                    SettingRow(
                        icon = profileIcon("notifications"),
                        title = "Notifications",
                        subtitle = "Rappels et alertes quotidiennes",
                        onClick = { notificationsEnabled = !notificationsEnabled },
                        trailing = {
                            Switch(
                                checked = notificationsEnabled,
                                onCheckedChange = { notificationsEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                                ),
                            )
                        },
                    )
                    SettingDivider()
                    SettingRow(
                        icon = profileIcon("dark"),
                        title = "Theme sombre",
                        subtitle = "Basculer vers le theme sombre",
                        onClick = { darkModeEnabled = !darkModeEnabled },
                        trailing = {
                            Switch(
                                checked = darkModeEnabled,
                                onCheckedChange = { darkModeEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                                ),
                            )
                        },
                    )
                }

                SettingSection(title = "Aide") {
                    SettingRow(
                        icon = profileIcon("help"),
                        title = "Aide et FAQ",
                        subtitle = "Obtenir de l'aide sur l'application",
                        onClick = {},
                    )
                    SettingDivider()
                    SettingRow(
                        icon = profileIcon("privacy"),
                        title = "Confidentialite",
                        subtitle = "Comment vos donnees sont protegees",
                        onClick = {},
                    )
                    SettingDivider()
                    SettingRow(
                        icon = profileIcon("about"),
                        title = "A propos",
                        subtitle = "Version 1.0.0",
                        onClick = {},
                    )
                }

                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.06f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)),
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = "100% prive et local",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Toutes vos donnees sante restent exclusivement sur cet appareil. Pas de cloud, pas d'acces tiers. ${lastSyncLabel ?: "Aucune synchronisation terminee pour le moment."}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }

    selectedMetricSetting?.let { setting ->
        AlertDialog(
            onDismissRequest = { selectedMetricSetting = null },
            title = {
                Text(
                    text = "Source pour ${setting.title}",
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    setting.options.forEach { option ->
                        SourceOptionRow(
                            option = option,
                            selected = option.sourceId == setting.selectedSourceId,
                            onClick = {
                                onSelectMetricSource(setting.metricType, option.sourceId)
                                selectedMetricSetting = null
                            },
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedMetricSetting = null }) {
                    Text("Fermer")
                }
            },
        )
    }
}

@Composable
private fun SettingSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)),
        ) {
            Column(content = content)
        }
    }
}

@Composable
private fun SummaryStat(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SettingDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .height(1.dp)
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
    )
}

@Composable
private fun SourceOptionRow(
    option: MetricSourceOptionUi,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            RadioButton(
                selected = selected,
                onClick = null,
            )
            Text(
                text = option.label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            )
        }
    }
}
