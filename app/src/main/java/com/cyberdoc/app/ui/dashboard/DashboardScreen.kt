package com.cyberdoc.app.ui.dashboard

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cyberdoc.app.domain.model.DashboardMetric
import com.cyberdoc.app.domain.model.QualityFlag
import com.cyberdoc.app.domain.model.TrendPoint
import com.cyberdoc.app.domain.model.TrendRange

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val context = LocalContext.current
    var showWeightDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
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
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    TrendRange.entries.forEach { range ->
                        val selected = range == uiState.selectedRange
                        if (selected) {
                            Button(onClick = { viewModel.selectRange(range) }) {
                                Text(range.label)
                            }
                        } else {
                            OutlinedButton(onClick = { viewModel.selectRange(range) }) {
                                Text(range.label)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { showWeightDialog = true },
                ) {
                    Text("Saisir le poids")
                }
            }
        }

        uiState.metrics.forEach { metric ->
            DashboardMetricCard(metric = metric)
        }
    }

    if (showWeightDialog) {
        ManualWeightDialog(
            isSaving = uiState.isSavingWeight,
            onDismiss = { showWeightDialog = false },
            onSave = { input ->
                val saved = viewModel.saveManualWeight(input)
                if (saved) {
                    showWeightDialog = false
                } else {
                    Toast.makeText(
                        context,
                        "Entre un poids valide en kilogrammes",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            },
        )
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
                text = metric.deltaLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary,
            )
            Spacer(modifier = Modifier.height(12.dp))
            TrendBars(metric.trendPoints)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = metric.trendLabel,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
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
        }
    }
}

@Composable
private fun TrendBars(points: List<TrendPoint>) {
    val maxValue = points.mapNotNull { it.value }.maxOrNull() ?: 0.0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            points.forEach { point ->
                val value = point.value
                val barHeight = when {
                    value == null -> 16.dp
                    maxValue <= 0.0 -> 18.dp
                    else -> (18f + ((value / maxValue) * 54).toFloat()).dp
                }
                val color = when {
                    value == null -> MaterialTheme.colorScheme.outlineVariant
                    else -> MaterialTheme.colorScheme.primary
                }

                Box(
                    modifier = Modifier
                        .width(8.dp)
                        .height(barHeight)
                        .background(color = color, shape = RoundedCornerShape(99.dp)),
                )
            }
        }
    }
}

@Composable
private fun ManualWeightDialog(
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
) {
    var input by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Saisie manuelle du poids") },
        text = {
            Column {
                Text(
                    text = "Entre ton poids du jour en kilogrammes. La saisie manuelle devient la valeur retenue pour aujourd'hui.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    singleLine = true,
                    label = { Text("Poids (kg)") },
                    keyboardOptions = KeyboardOptions.Default,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(input)
                },
                enabled = !isSaving,
            ) {
                Text(if (isSaving) "Enregistrement..." else "Enregistrer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSaving) {
                Text("Annuler")
            }
        },
    )
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
