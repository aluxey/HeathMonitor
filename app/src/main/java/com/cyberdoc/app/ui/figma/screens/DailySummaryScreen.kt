package com.cyberdoc.app.ui.figma.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cyberdoc.app.ui.figma.components.SmallBackChip
import com.cyberdoc.app.ui.figma.components.TrendChart
import com.cyberdoc.app.ui.figma.components.heroIcon
import com.cyberdoc.app.ui.figma.components.metricIcon
import com.cyberdoc.app.ui.theme.Chart1
import com.cyberdoc.app.ui.theme.Chart2
import com.cyberdoc.app.ui.theme.Chart3
import com.cyberdoc.app.ui.theme.Chart4
import com.cyberdoc.app.ui.theme.Chart5
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private data class SummaryMetric(
    val id: String,
    val label: String,
    val value: String,
    val target: String,
    val achieved: Boolean,
    val color: androidx.compose.ui.graphics.Color,
)

@Composable
fun DailySummaryScreen(onBack: () -> Unit) {
    val todayLabel = remember {
        LocalDate.now().format(
            DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault()),
        )
    }
    val todayMetrics = remember {
        listOf(
            SummaryMetric("steps", "Steps", "8,547", "10,000", achieved = false, color = Chart1),
            SummaryMetric("heart", "Heart Rate", "72 bpm", "Normal", achieved = true, color = Chart4),
            SummaryMetric("sleep", "Sleep", "7.5 hrs", "8 hrs", achieved = false, color = Chart5),
            SummaryMetric("activity", "Active Minutes", "45 min", "60 min", achieved = false, color = Chart2),
            SummaryMetric("hydration", "Hydration", "1.8 L", "2.5 L", achieved = false, color = Chart3),
        )
    }
    val weeklyScores = remember { listOf(85f, 78f, 92f, 88f, 95f, 90f, 87f) }
    val achievedCount = todayMetrics.count { it.achieved }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
            ) {
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        SmallBackChip(onClick = onBack)
                        Column {
                            Text(
                                text = "Daily Summary",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = todayLabel,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.78f),
                                    ),
                                ),
                                shape = RoundedCornerShape(28.dp),
                            )
                            .padding(24.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "Wellness Score",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                                )
                                Text(
                                    text = "87",
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                                Text(
                                    text = "$achievedCount of ${todayMetrics.size} targets met",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.18f),
                                        shape = RoundedCornerShape(24.dp),
                                    )
                                    .padding(16.dp),
                            ) {
                                Icon(
                                    imageVector = heroIcon("calendar"),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(28.dp),
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                Text(
                    text = "Today's Activity",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )

                todayMetrics.forEach { metric ->
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = metricIcon(metric.id),
                                contentDescription = null,
                                tint = metric.color,
                                modifier = Modifier.size(24.dp),
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = metric.label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Text(
                                    text = metric.value,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Target",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Text(
                                    text = metric.target,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                )
                            }
                            Icon(
                                imageVector = if (metric.achieved) {
                                    Icons.Rounded.CheckCircleOutline
                                } else {
                                    Icons.Rounded.RadioButtonUnchecked
                                },
                                contentDescription = null,
                                tint = if (metric.achieved) metric.color else MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                Text(
                    text = "Weekly Wellness",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )

                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)),
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        TrendChart(
                            data = weeklyScores,
                            labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            SummaryScore(label = "Weekly average", value = "87.9")
                            SummaryScore(label = "Best day", value = "95")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryScore(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}
