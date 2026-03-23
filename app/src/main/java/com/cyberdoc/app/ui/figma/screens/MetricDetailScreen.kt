package com.cyberdoc.app.ui.figma.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.cyberdoc.app.ui.figma.components.PeriodChip
import com.cyberdoc.app.ui.figma.components.SmallBackChip
import com.cyberdoc.app.ui.figma.components.StatValueCard
import com.cyberdoc.app.ui.figma.components.TrendChart
import com.cyberdoc.app.ui.figma.model.MetricUi
import com.cyberdoc.app.ui.figma.navigation.Period
import com.cyberdoc.app.ui.theme.Chart2
import java.util.Locale

@Composable
fun MetricDetailScreen(metric: MetricUi, onBack: () -> Unit) {
    var period by remember { mutableStateOf(Period.WEEK) }
    val data = if (period == Period.WEEK) metric.weekData else metric.monthData
    val goalProgress = metric.goal?.let { (metric.value.toFloatOrNull() ?: 0f) / it }?.coerceIn(0f, 1f) ?: 0f

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SmallBackChip(onClick = onBack)
                Text(metric.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            }
        }

        item {
            Surface(shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                androidx.compose.foundation.layout.Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Current", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(metric.value, style = MaterialTheme.typography.displaySmall, color = metric.color, fontWeight = FontWeight.Bold)
                        Text(metric.unit, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    metric.trendLabel?.let {
                        Text(
                            it + " vs last week",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (metric.trendUp) Chart2 else MaterialTheme.colorScheme.error,
                        )
                    }
                    if (metric.goal != null) {
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

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PeriodChip("Week", selected = period == Period.WEEK) { period = Period.WEEK }
                PeriodChip("Month", selected = period == Period.MONTH) { period = Period.MONTH }
            }
        }

        item {
            Surface(shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp), color = MaterialTheme.colorScheme.surface) {
                androidx.compose.foundation.layout.Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(if (period == Period.WEEK) "Last 7 days" else "Last 30 days", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    TrendChart(data = data, color = metric.color)
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatValueCard(
                    label = "Average",
                    value = String.format(Locale.US, "%.1f", data.sum() / data.size),
                    unit = metric.unit,
                    modifier = Modifier.weight(1f),
                )
                StatValueCard(
                    label = "Best",
                    value = data.maxOrNull()?.let { String.format(Locale.US, "%.1f", it) } ?: "-",
                    unit = metric.unit,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
