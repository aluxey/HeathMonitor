package com.cyberdoc.app.ui.figma.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
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
import com.cyberdoc.app.ui.figma.components.SegmentedControl
import com.cyberdoc.app.ui.figma.components.SmallBackChip
import com.cyberdoc.app.ui.figma.components.StatValueCard
import com.cyberdoc.app.ui.figma.components.StatusBadge
import com.cyberdoc.app.ui.figma.components.TrendChart
import com.cyberdoc.app.ui.figma.components.TrendLabel
import com.cyberdoc.app.ui.figma.model.MetricUi
import com.cyberdoc.app.ui.figma.navigation.Period
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun MetricDetailScreen(metric: MetricUi, onBack: () -> Unit) {
    var period by remember { mutableStateOf(Period.WEEK) }
    val data = if (period == Period.WEEK) metric.weekData else metric.monthData
    val hasHistory = data.isNotEmpty()
    val labels = if (period == Period.WEEK) {
        listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    } else {
        listOf("1", "5", "10", "15", "20", "25", "30").take(data.size)
    }
    val currentValue = metric.value.toFloatOrNull()
    val goalProgress = metric.goal?.let { goal ->
        currentValue?.let { (it / goal).coerceIn(0f, 1f) }
    } ?: 0f

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(18.dp),
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
                        Text(
                            text = metric.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top,
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = "Valeur actuelle",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                    Row(
                                        verticalAlignment = Alignment.Bottom,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {
                                        Text(
                                            text = metric.value,
                                            style = MaterialTheme.typography.displaySmall,
                                            color = metric.color,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        Text(
                                            text = metric.unit,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                                StatusBadge(source = metric.source)
                            }

                            metric.trendLabel?.let {
                                TrendLabel(label = it, trendUp = metric.trendUp)
                            }

                            metric.emptyStateMessage?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }

                            metric.goal?.let { goal ->
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Text(
                                            text = "Objectif journalier",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                        Text(
                                            text = currentValue?.let {
                                                "${(goalProgress * 100).roundToInt()}% atteint"
                                            } ?: "Pas encore de donnees",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium,
                                        )
                                    }
                                    LinearProgressIndicator(
                                        progress = { goalProgress },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp),
                                        color = metric.color,
                                        trackColor = MaterialTheme.colorScheme.surface,
                                    )
                                }
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
                SegmentedControl(
                    selectedIndex = if (period == Period.WEEK) 0 else 1,
                    labels = listOf("Semaine", "Mois"),
                    onSelect = { period = if (it == 0) Period.WEEK else Period.MONTH },
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
                        Text(
                            text = if (period == Period.WEEK) "7 derniers jours" else "30 derniers jours",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                        )
                        if (hasHistory) {
                            TrendChart(
                                data = data,
                                labels = labels,
                                color = metric.color,
                            )
                        } else {
                            Text(
                                text = metric.emptyStateMessage ?: "Aucun historique disponible pour cette metrique.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                if (hasHistory) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        StatValueCard(
                            label = "Moyenne",
                            value = formatStatValue(data.average()),
                            unit = metric.unit,
                            modifier = Modifier.weight(1f),
                        )
                        StatValueCard(
                            label = "Meilleur jour",
                            value = formatStatValue((data.maxOrNull() ?: 0f).toDouble()),
                            unit = metric.unit,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

private fun formatStatValue(value: Double): String =
    if (value % 1.0 == 0.0) {
        value.roundToInt().toString()
    } else {
        String.format(Locale.US, "%.1f", value)
    }
