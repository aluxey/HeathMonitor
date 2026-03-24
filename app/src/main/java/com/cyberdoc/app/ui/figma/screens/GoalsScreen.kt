package com.cyberdoc.app.ui.figma.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.cyberdoc.app.domain.model.MetricType
import com.cyberdoc.app.ui.figma.components.GoalCard
import com.cyberdoc.app.ui.figma.components.SectionHeader
import com.cyberdoc.app.ui.figma.components.heroIcon
import com.cyberdoc.app.ui.figma.model.GoalDraft
import com.cyberdoc.app.ui.figma.model.GoalUi
import com.cyberdoc.app.ui.figma.model.editableGoalMetricTypes
import com.cyberdoc.app.ui.figma.model.metricDisplayUnit
import com.cyberdoc.app.ui.figma.model.metricTitle
import kotlin.math.roundToInt

@Composable
fun GoalsScreen(
    goals: List<GoalUi>,
    isSaving: Boolean,
    feedbackMessage: String?,
    errorMessage: String?,
    onSaveGoal: (GoalDraft) -> Unit,
) {
    val availableMetrics = remember { editableGoalMetricTypes() }
    var editorOpen by remember { mutableStateOf(false) }
    var selectedMetricType by remember { mutableStateOf(availableMetrics.first()) }
    var targetValue by remember { mutableStateOf("") }

    LaunchedEffect(feedbackMessage) {
        if (feedbackMessage != null) {
            editorOpen = false
        }
    }

    LaunchedEffect(goals, selectedMetricType) {
        val existingGoal = goals.firstOrNull { it.metricType == selectedMetricType }
        targetValue = existingGoal?.target
            ?.takeIf { it > 0f }
            ?.let { target ->
                if (target % 1f == 0f) {
                    target.toInt().toString()
                } else {
                    target.toString()
                }
            }
            ?: ""
    }

    val achievedGoals = goals.count { it.target > 0f && it.current >= it.target }
    val achievementRate = if (goals.isEmpty()) 0 else {
        ((achievedGoals.toFloat() / goals.size) * 100).roundToInt()
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            Surface(
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
                        text = "Track your daily targets with local data and Health Connect sync.",
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
                            brush = androidx.compose.ui.graphics.Brush.linearGradient(
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
                                    shape = RoundedCornerShape(18.dp),
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
                    actionLabel = if (editorOpen) "Close editor" else "Add goal",
                    actionIcon = Icons.Rounded.Add,
                    onAction = { editorOpen = !editorOpen },
                )

                AnimatedVisibility(visible = editorOpen) {
                    GoalEditorCard(
                        availableMetrics = availableMetrics,
                        selectedMetricType = selectedMetricType,
                        targetValue = targetValue,
                        isSaving = isSaving,
                        feedbackMessage = feedbackMessage,
                        errorMessage = errorMessage,
                        onSelectMetric = { selectedMetricType = it },
                        onTargetChange = { targetValue = it },
                        onSaveGoal = {
                            targetValue.toDoubleOrNull()?.let { parsedValue ->
                                onSaveGoal(
                                    GoalDraft(
                                        metricType = selectedMetricType,
                                        targetValue = parsedValue,
                                    ),
                                )
                            }
                        },
                    )
                }

                if (goals.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)),
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                text = "No goals configured yet",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = "Create a first target to compare your synced data against a daily objective.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                } else {
                    goals.forEach { goal ->
                        GoalCard(
                            metricId = goal.metricId,
                            title = goal.title,
                            current = goal.current,
                            target = goal.target,
                            unit = goal.unit,
                            color = goal.color,
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

@Composable
private fun GoalEditorCard(
    availableMetrics: List<MetricType>,
    selectedMetricType: MetricType,
    targetValue: String,
    isSaving: Boolean,
    feedbackMessage: String?,
    errorMessage: String?,
    onSelectMetric: (MetricType) -> Unit,
    onTargetChange: (String) -> Unit,
    onSaveGoal: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Edit daily target",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            availableMetrics.chunked(2).forEach { rowMetrics ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    rowMetrics.forEach { metricType ->
                        val selected = metricType == selectedMetricType
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSelectMetric(metricType) },
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (selected) 0.55f else 0.2f),
                            border = BorderStroke(
                                width = if (selected) 1.5.dp else 1.dp,
                                color = if (selected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                },
                            ),
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Text(
                                    text = metricTitle(metricType),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Text(
                                    text = metricDisplayUnit(metricType),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }

                    if (rowMetrics.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            OutlinedTextField(
                value = targetValue,
                onValueChange = onTargetChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Daily target") },
                singleLine = true,
                trailingIcon = {
                    Text(
                        text = metricDisplayUnit(selectedMetricType),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(18.dp),
            )

            if (feedbackMessage != null) {
                Text(
                    text = feedbackMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            if (errorMessage != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ErrorOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }

            Button(
                onClick = onSaveGoal,
                enabled = targetValue.toDoubleOrNull() != null && !isSaving,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
            ) {
                Text(if (isSaving) "Saving..." else "Save goal")
            }
        }
    }
}
