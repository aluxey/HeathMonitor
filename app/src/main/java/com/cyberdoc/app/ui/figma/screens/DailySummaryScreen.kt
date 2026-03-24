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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cyberdoc.app.ui.figma.components.SmallBackChip
import com.cyberdoc.app.ui.figma.components.heroIcon
import com.cyberdoc.app.ui.figma.components.metricIcon
import com.cyberdoc.app.ui.figma.model.GoalUi
import com.cyberdoc.app.ui.figma.model.MetricUi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

private data class SummaryMetric(
    val id: String,
    val label: String,
    val value: String,
    val target: String,
    val statusLabel: String,
    val hasData: Boolean,
    val achieved: Boolean,
    val color: Color,
)

@Composable
fun DailySummaryScreen(
    metrics: List<MetricUi>,
    goals: List<GoalUi>,
    lastSyncLabel: String?,
    onBack: () -> Unit,
) {
    val todayLabel = remember {
        LocalDate.now().format(
            DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault()),
        )
    }
    val goalByMetricId = remember(goals) { goals.associateBy { it.metricId } }
    val todayMetrics = remember(metrics, goalByMetricId) {
        metrics.map { metric ->
            val goal = goalByMetricId[metric.id]
            val currentValue = metric.value.toFloatOrNull()
            val hasData = currentValue != null
            val goalReached = goal != null && currentValue != null && currentValue >= goal.target

            SummaryMetric(
                id = metric.id,
                label = metric.title,
                value = if (hasData) displayMetricValue(metric.value, metric.unit) else "xx",
                target = goal?.let { formatGoalValue(it.target, it.unit) } ?: "Not set",
                statusLabel = when {
                    !hasData -> "No data"
                    goal == null -> "No goal"
                    goalReached -> "Reached"
                    else -> "In progress"
                },
                hasData = hasData,
                achieved = goalReached,
                color = metric.color,
            )
        }
    }
    val syncedMetricCount = todayMetrics.count { it.hasData }
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
                                    text = "Metrics synced today",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                                )
                                Text(
                                    text = syncedMetricCount.toString(),
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                                Text(
                                    text = if (goals.isEmpty()) {
                                        "No goals configured yet"
                                    } else {
                                        "$achievedCount of ${goals.size} goals reached"
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                                Text(
                                    text = lastSyncLabel ?: "No sync has completed yet",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
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
                    text = "Today's Metrics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )

                if (todayMetrics.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)),
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                text = "No metrics available yet",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = "Connect Health Connect or add a manual entry to start filling this summary.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                } else {
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
                                    Text(
                                        text = metric.statusLabel,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (metric.achieved) {
                                            metric.color
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        },
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
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun displayMetricValue(value: String, unit: String): String =
    if (unit.isBlank()) {
        value
    } else {
        "$value $unit"
    }

private fun formatGoalValue(value: Float, unit: String): String {
    val formattedValue = if (value % 1f == 0f) {
        value.roundToInt().toString()
    } else {
        String.format(Locale.US, "%.1f", value)
    }

    return if (unit.isBlank()) {
        formattedValue
    } else {
        "$formattedValue $unit"
    }
}
