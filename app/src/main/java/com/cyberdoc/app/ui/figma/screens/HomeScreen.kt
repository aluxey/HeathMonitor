package com.cyberdoc.app.ui.figma.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.cyberdoc.app.ui.figma.components.MetricCard
import com.cyberdoc.app.ui.figma.components.QuickStatCard
import com.cyberdoc.app.ui.figma.components.SectionHeader
import com.cyberdoc.app.ui.figma.model.MetricUi

@Composable
fun HomeScreen(
    metrics: List<MetricUi>,
    todayLabel: String,
    onOpenSummary: () -> Unit,
    onOpenManual: () -> Unit,
    onOpenGoals: () -> Unit,
    onOpenHealthConnect: () -> Unit,
    onOpenMetric: (String) -> Unit,
) {
    val stepsMetric = metrics.firstOrNull { it.id == "steps" }
    val heartMetric = metrics.firstOrNull { it.id == "heart" }

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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Today",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = todayLabel,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }

                        Surface(
                            modifier = Modifier.clickable(onClick = onOpenSummary),
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        ) {
                            Box(
                                modifier = Modifier.padding(12.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.CalendarMonth,
                                    contentDescription = "Summary",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp),
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        QuickStatCard(
                            metricId = "steps",
                            label = "Steps today",
                            value = stepsMetric?.value ?: "--",
                            color = stepsMetric?.color ?: MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f),
                        )
                        QuickStatCard(
                            metricId = "heart",
                            label = "Avg heart rate",
                            value = buildString {
                                val value = heartMetric?.value ?: "--"
                                append(value)
                                if (value != "--") append(" bpm")
                            },
                            color = heartMetric?.color ?: MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }

        item {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                SectionHeader(
                    title = "Health Metrics",
                    actionLabel = "Add entry",
                    actionIcon = Icons.Rounded.Add,
                    onAction = onOpenManual,
                )

                if (metrics.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)),
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                text = "No metrics available yet",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = "Connect Health Connect or add a manual entry to populate the dashboard.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                } else {
                    metrics.forEach { metric ->
                        MetricCard(metric = metric, onClick = { onOpenMetric(metric.id) })
                    }
                }
            }
        }

        item {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    QuickActionTile(
                        title = "View Goals",
                        subtitle = "Track progress",
                        icon = Icons.Rounded.TrendingUp,
                        modifier = Modifier.weight(1f),
                        onClick = onOpenGoals,
                    )
                    QuickActionTile(
                        title = "Health Connect",
                        subtitle = "Manage sync",
                        icon = Icons.Rounded.CloudDownload,
                        modifier = Modifier.weight(1f),
                        onClick = onOpenHealthConnect,
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

@Composable
private fun QuickActionTile(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(16.dp),
                    )
                    .padding(10.dp),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp),
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
