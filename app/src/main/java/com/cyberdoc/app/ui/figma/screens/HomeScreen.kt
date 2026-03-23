package com.cyberdoc.app.ui.figma.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cyberdoc.app.ui.figma.components.ActionCard
import com.cyberdoc.app.ui.figma.components.MetricCard
import com.cyberdoc.app.ui.figma.components.StatWidget
import com.cyberdoc.app.ui.figma.model.MetricUi
import com.cyberdoc.app.ui.theme.Chart1
import com.cyberdoc.app.ui.theme.Chart4

@Composable
fun HomeScreen(
    metrics: List<MetricUi>,
    onOpenSummary: () -> Unit,
    onOpenManual: () -> Unit,
    onOpenGoals: () -> Unit,
    onOpenHealthConnect: () -> Unit,
    onOpenMetric: (String) -> Unit,
) {
    val stepsMetric = metrics.first { it.id == "steps" }
    val heartMetric = metrics.first { it.id == "heart" }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 18.dp),
    ) {
        item {
            Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 1.dp) {
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text("Today", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Text("Monday, March 23", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Surface(
                            modifier = Modifier.clickable(onClick = onOpenSummary),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        ) {
                            Text(
                                text = "Summary",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatWidget(label = "Steps today", value = stepsMetric.value, color = Chart1, modifier = Modifier.weight(1f))
                        StatWidget(label = "Avg heart rate", value = heartMetric.value + " bpm", color = Chart4, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Health Metrics", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = "Add entry",
                        modifier = Modifier.clickable(onClick = onOpenManual),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                    )
                }

                metrics.forEach { metric ->
                    MetricCard(metric = metric, onClick = { onOpenMetric(metric.id) })
                }
            }
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Quick Actions", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ActionCard(label = "View Goals", modifier = Modifier.weight(1f), onClick = onOpenGoals)
                    ActionCard(label = "Health Connect", modifier = Modifier.weight(1f), onClick = onOpenHealthConnect)
                }
            }
        }
    }
}
