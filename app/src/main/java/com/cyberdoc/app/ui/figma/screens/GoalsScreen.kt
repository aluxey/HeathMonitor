package com.cyberdoc.app.ui.figma.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cyberdoc.app.ui.figma.components.GoalCard
import kotlin.math.roundToInt

@Composable
fun GoalsScreen() {
    val goals = remember {
        listOf(
            Triple("Daily Steps", 8547f, 10000f),
            Triple("Sleep Duration", 7.5f, 8f),
            Triple("Active Minutes", 45f, 60f),
            Triple("Water Intake", 1.8f, 2.5f),
        )
    }
    val achieved = goals.count { it.second >= it.third }
    val rate = ((achieved.toFloat() / goals.size) * 100).roundToInt()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text("Goals and Progress", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Track your wellness targets", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            ) {
                androidx.compose.foundation.layout.Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Achievement rate", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f), style = MaterialTheme.typography.bodySmall)
                    Text("$rate%", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
                    Text("$achieved of ${goals.size} goals achieved today", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        items(goals) { goal ->
            GoalCard(title = goal.first, current = goal.second, target = goal.third)
        }
    }
}
