package com.cyberdoc.app.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cyberdoc.app.domain.model.DashboardMetric
import com.cyberdoc.app.domain.model.QualityFlag

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                shape = RoundedCornerShape(28.dp),
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Vue du jour",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Date active: ${uiState.dateLabel}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Le dashboard est deja branche sur les agrégats locaux et les objectifs.",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }

        items(uiState.metrics) { metric ->
            DashboardMetricCard(metric = metric)
        }
    }
}

@Composable
private fun DashboardMetricCard(
    metric: DashboardMetric,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = RoundedCornerShape(24.dp),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(
                        text = metric.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = metric.valueLabel,
                        style = MaterialTheme.typography.headlineSmall,
                    )
                }
                QualityPill(metric.qualityFlag)
            }
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                progress = { metric.progress },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = metric.targetLabel, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Source ${metric.sourceLabel}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = metric.freshnessLabel,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = metric.trendLabel,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.tertiary,
            )
        }
    }
}

@Composable
private fun QualityPill(qualityFlag: QualityFlag) {
    val (label, color) = when (qualityFlag) {
        QualityFlag.OK -> "OK" to MaterialTheme.colorScheme.primary
        QualityFlag.PARTIAL -> "Partiel" to MaterialTheme.colorScheme.tertiary
        QualityFlag.CONFLICT -> "Conflit" to MaterialTheme.colorScheme.error
        QualityFlag.MISSING -> "Manquant" to MaterialTheme.colorScheme.outline
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.14f)),
        shape = RoundedCornerShape(999.dp),
    ) {
        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = color,
            )
        }
    }
}
