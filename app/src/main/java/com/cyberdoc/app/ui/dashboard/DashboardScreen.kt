package com.cyberdoc.app.ui.dashboard

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cyberdoc.app.domain.model.DashboardMetric
import com.cyberdoc.app.domain.model.MetricType
import com.cyberdoc.app.domain.model.QualityFlag
import com.cyberdoc.app.domain.model.TrendPoint
import com.cyberdoc.app.domain.model.TrendRange
import kotlin.math.roundToInt

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val context = LocalContext.current
    var showWeightSheet by remember { mutableStateOf(false) }
    var selectedMetric by remember { mutableStateOf<DashboardMetric?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        DashboardHero(
            dateLabel = uiState.dateLabel,
            metrics = uiState.metrics,
            selectedRange = uiState.selectedRange,
            onRangeSelected = viewModel::selectRange,
        )

        QuickActionsRow(
            onAddWeight = { showWeightSheet = true },
            onOpenTrend = { selectedMetric = uiState.metrics.firstOrNull() },
        )

        MetricWidgetGrid(
            metrics = uiState.metrics,
            onMetricSelected = { selectedMetric = it },
        )

        DataHealthSection(metrics = uiState.metrics)
    }

    if (showWeightSheet) {
        ManualWeightSheet(
            isSaving = uiState.isSavingWeight,
            onDismiss = { showWeightSheet = false },
            onSave = { input ->
                val saved = viewModel.saveManualWeight(input)
                if (saved) {
                    showWeightSheet = false
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

    selectedMetric?.let { metric ->
        MetricDetailSheet(
            metric = metric,
            onDismiss = { selectedMetric = null },
            onAddWeight = { showWeightSheet = true },
        )
    }
}

@Composable
private fun DashboardHero(
    dateLabel: String,
    metrics: List<DashboardMetric>,
    selectedRange: TrendRange,
    onRangeSelected: (TrendRange) -> Unit,
) {
    val completedMetrics = metrics.count { it.qualityFlag == QualityFlag.OK }
    val missingMetrics = metrics.count { it.qualityFlag == QualityFlag.MISSING }
    val focusMetric = metrics.maxByOrNull { it.progress }

    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Text(
                text = "Aujourd'hui • $dateLabel",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Bonjour. Tes indicateurs cles sont condensés en widgets lisibles et rassurants.",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "Resume du jour",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = focusMetric?.let { "${it.title} en tete avec ${it.valueLabel}. ${it.deltaLabel}." }
                            ?: "Ajoute une premiere donnee pour personnaliser ton dashboard.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        DashboardStatPill("$completedMetrics complets", "widgets OK")
                        DashboardStatPill("$missingMetrics a completer", "donnees manquantes")
                    }
                }
            }
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                TrendRange.entries.forEach { range ->
                    FilterChipWidget(
                        label = range.label,
                        selected = range == selectedRange,
                        onClick = { onRangeSelected(range) },
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionsRow(
    onAddWeight: () -> Unit,
    onOpenTrend: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Actions rapides",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            QuickActionCard("Saisir poids", "Ajout manuel ultra rapide", onAddWeight)
            QuickActionCard("Voir tendances", "Ouvre le detail du widget principal", onOpenTrend)
            QuickActionCard("Synchroniser", "Depuis l'ecran Sources", onClick = {})
            QuickActionCard("Objectifs", "Ajuste tes reperes bientot", onClick = {})
        }
    }
}

@Composable
private fun MetricWidgetGrid(
    metrics: List<DashboardMetric>,
    onMetricSelected: (DashboardMetric) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Widgets KPI",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
        metrics.chunked(2).forEach { rowMetrics ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                rowMetrics.forEach { metric ->
                    DashboardMetricCard(
                        modifier = Modifier.weight(1f),
                        metric = metric,
                        onClick = { onMetricSelected(metric) },
                    )
                }
                if (rowMetrics.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun DataHealthSection(metrics: List<DashboardMetric>) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Etat des sources",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            metrics.forEach { metric ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(metric.title, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "${metric.sourceLabel} • ${metric.freshnessLabel}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    QualityPill(metric.qualityFlag)
                }
            }
        }
    }
}

@Composable
private fun DashboardMetricCard(
    modifier: Modifier = Modifier,
    metric: DashboardMetric,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = metricLabel(metric.metricType),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = metric.valueLabel,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                }
                QualityPill(metric.qualityFlag)
            }
            Text(
                text = metric.targetLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            LinearProgressIndicator(
                progress = { metric.progress.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = qualityColor(metric.qualityFlag),
                trackColor = MaterialTheme.colorScheme.secondaryContainer,
            )
            Text(
                text = metric.deltaLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = qualityColor(metric.qualityFlag),
                fontWeight = FontWeight.Medium,
            )
            TrendMicroChart(points = metric.trendPoints)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                MetaPill(metric.sourceLabel)
                MetaPill(metric.freshnessLabel)
            }
        }
    }
}

@Composable
private fun TrendMicroChart(points: List<TrendPoint>) {
    val values = points.mapNotNull { it.value }
    val minValue = values.minOrNull() ?: 0.0
    val maxValue = values.maxOrNull() ?: 0.0
    val outlineColor = MaterialTheme.colorScheme.outlineVariant
    val lineColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
        ) {
            if (values.isEmpty()) {
                drawRoundRect(
                    color = outlineColor,
                    cornerRadius = CornerRadius(20f, 20f),
                )
                return@Canvas
            }

            val path = Path()
            val pointsWithValue = points.mapIndexedNotNull { index, point ->
                point.value?.let { index to it }
            }
            pointsWithValue.forEachIndexed { order, (index, value) ->
                val x = if (points.size <= 1) 0f else size.width * index / (points.size - 1).toFloat()
                val normalized = if (maxValue == minValue) 0.5f else ((value - minValue) / (maxValue - minValue)).toFloat()
                val y = size.height - (normalized * size.height)
                if (order == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }

            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 6f),
            )

            pointsWithValue.forEach { (index, value) ->
                val x = if (points.size <= 1) 0f else size.width * index / (points.size - 1).toFloat()
                val normalized = if (maxValue == minValue) 0.5f else ((value - minValue) / (maxValue - minValue)).toFloat()
                val y = size.height - (normalized * size.height)
                drawCircle(
                    color = surfaceColor,
                    radius = 7f,
                    center = Offset(x, y),
                )
                drawCircle(
                    color = lineColor,
                    radius = 4f,
                    center = Offset(x, y),
                )
            }
        }
        Text(
            text = points.lastOrNull()?.dateLabel?.let { "Tendance jusqu'a $it" } ?: "Aucune tendance",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun MetricDetailSheet(
    metric: DashboardMetric,
    onDismiss: () -> Unit,
    onAddWeight: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = metric.title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = metric.valueLabel,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = metric.deltaLabel,
                style = MaterialTheme.typography.titleMedium,
                color = qualityColor(metric.qualityFlag),
            )
            TrendMicroChart(points = metric.trendPoints)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetaPill(metric.targetLabel)
                MetaPill(metric.sourceLabel)
            }
            DetailBlock("Comparaison a l'objectif", "${(metric.progress * 100).roundToInt()}% du repere atteint. ${metric.targetLabel}")
            DetailBlock("Origine des donnees", "Source ${metric.sourceLabel}. ${metric.freshnessLabel}.")
            DetailBlock("Historique lisible", metric.trendPoints.joinToString(" • ") { point ->
                "${point.dateLabel}: ${point.value?.roundToInt()?.toString() ?: "--"}"
            })
            if (metric.metricType == MetricType.WEIGHT) {
                Button(onClick = onAddWeight, modifier = Modifier.fillMaxWidth()) {
                    Text("Ajouter une mesure manuelle")
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ManualWeightSheet(
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
) {
    var input by remember { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Saisie manuelle",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "Ajoute ton poids en quelques secondes. Meme sans synchro, la carte reste utile et a jour.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Surface(shape = RoundedCornerShape(24.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Poids du jour", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        singleLine = true,
                        label = { Text("Kilogrammes") },
                        keyboardOptions = KeyboardOptions.Default,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Button(onClick = { onSave(input) }, enabled = !isSaving, modifier = Modifier.fillMaxWidth()) {
                        Text(if (isSaving) "Enregistrement..." else "Enregistrer rapidement")
                    }
                    TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End), enabled = !isSaving) {
                        Text("Fermer")
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun QuickActionCard(title: String, subtitle: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.width(182.dp).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun DashboardStatPill(value: String, label: String) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun FilterChipWidget(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(999.dp),
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
        contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
private fun DetailBlock(title: String, text: String) {
    Surface(shape = RoundedCornerShape(24.dp), color = MaterialTheme.colorScheme.surface) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun MetaPill(label: String) {
    Surface(shape = RoundedCornerShape(999.dp), color = MaterialTheme.colorScheme.secondaryContainer) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun QualityPill(qualityFlag: QualityFlag) {
    val label = when (qualityFlag) {
        QualityFlag.OK -> "OK"
        QualityFlag.PARTIAL -> "Incomplet"
        QualityFlag.CONFLICT -> "Conflit"
        QualityFlag.MISSING -> "Manquant"
    }

    Surface(
        shape = RoundedCornerShape(999.dp),
        color = qualityColor(qualityFlag).copy(alpha = 0.14f),
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            style = MaterialTheme.typography.labelLarge,
            color = qualityColor(qualityFlag),
        )
    }
}

@Composable
private fun qualityColor(qualityFlag: QualityFlag) = when (qualityFlag) {
    QualityFlag.OK -> MaterialTheme.colorScheme.primary
    QualityFlag.PARTIAL -> MaterialTheme.colorScheme.secondary
    QualityFlag.CONFLICT -> MaterialTheme.colorScheme.error
    QualityFlag.MISSING -> MaterialTheme.colorScheme.onSurfaceVariant
}

private fun metricLabel(metricType: MetricType): String = when (metricType) {
    MetricType.STEPS -> "Pas"
    MetricType.SLEEP_DURATION -> "Sommeil"
    MetricType.WEIGHT -> "Poids"
    MetricType.CALORIES_IN -> "Hydratation / nutrition"
    MetricType.EXERCISE_DURATION -> "Activite"
}
