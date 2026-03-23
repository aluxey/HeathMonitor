package com.cyberdoc.app.ui.figma.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cyberdoc.app.ui.figma.components.SmallBackChip
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DailySummaryScreen(onBack: () -> Unit) {
    val scores = remember { listOf(85, 78, 92, 88, 95, 90, 87) }
    val labels = remember { listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun") }
    val todayLabel = remember {
        LocalDate.now().format(
            DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault()),
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SmallBackChip(onClick = onBack)
                androidx.compose.foundation.layout.Column {
                    Text("Daily Summary", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Text(todayLabel, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        item {
            Card(shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)) {
                androidx.compose.foundation.layout.Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Wellness Score", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f))
                    Text("87", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                    Text("1 of 5 targets met", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
        item {
            Text("Weekly Wellness", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Surface(shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface) {
                androidx.compose.foundation.layout.Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    scores.forEachIndexed { index, score ->
                        val animated by animateFloatAsState(targetValue = score / 100f, label = "weekScore")
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(labels[index], style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(end = 6.dp))
                            LinearProgressIndicator(
                                progress = { animated },
                                modifier = Modifier.weight(1f).height(8.dp),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            )
                            Text(score.toString(), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
