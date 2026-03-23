package com.cyberdoc.app.ui.figma.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cyberdoc.app.ui.figma.components.GoalCard
import com.cyberdoc.app.ui.figma.components.SectionHeader
import com.cyberdoc.app.ui.figma.components.heroIcon
import com.cyberdoc.app.ui.theme.Chart1
import com.cyberdoc.app.ui.theme.Chart2
import com.cyberdoc.app.ui.theme.Chart4
import com.cyberdoc.app.ui.theme.Chart5
import kotlin.math.roundToInt

private data class GoalUi(
    val id: String,
    val title: String,
    val current: Float,
    val target: Float,
    val unit: String,
    val color: androidx.compose.ui.graphics.Color,
)

@Composable
fun GoalsScreen() {
    val goals = remember {
        listOf(
            GoalUi("steps", "Daily Steps", 8547f, 10000f, "steps", Chart1),
            GoalUi("sleep", "Sleep Duration", 7.5f, 8f, "hours", Chart5),
            GoalUi("activity", "Active Minutes", 45f, 60f, "minutes", Chart2),
            GoalUi("heart", "Resting Heart Rate", 72f, 65f, "bpm", Chart4),
        )
    }
    val achievedGoals = goals.count { it.current >= it.target }
    val achievementRate = ((achievedGoals.toFloat() / goals.size) * 100).roundToInt()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            androidx.compose.material3.Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
            ) {
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)) {
                    Text(
                        text = "Goals & Progress",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Track your wellness targets",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        item {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
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
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(28.dp),
                        )
                        .padding(24.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top,
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Achievement rate",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                            )
                            Text(
                                text = "$achievementRate%",
                                style = MaterialTheme.typography.displaySmall,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "$achievedGoals of ${goals.size} goals achieved today",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.18f),
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
                                )
                                .padding(14.dp),
                        ) {
                            Icon(
                                imageVector = heroIcon("trophy"),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(30.dp),
                            )
                        }
                    }
                }

                SectionHeader(
                    title = "Your Goals",
                    actionLabel = "Add goal",
                    actionIcon = Icons.Rounded.Add,
                    onAction = {},
                )

                goals.forEach { goal ->
                    GoalCard(
                        metricId = goal.id,
                        title = goal.title,
                        current = goal.current,
                        target = goal.target,
                        unit = goal.unit,
                        color = goal.color,
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}
