package com.cyberdoc.app.ui.figma.model

import androidx.compose.ui.graphics.Color
import com.cyberdoc.app.domain.model.DashboardSnapshot
import com.cyberdoc.app.domain.model.GoalProgress
import com.cyberdoc.app.domain.model.MetricType
import com.cyberdoc.app.ui.theme.Chart1
import com.cyberdoc.app.ui.theme.Chart2
import com.cyberdoc.app.ui.theme.Chart3
import com.cyberdoc.app.ui.theme.Chart4
import com.cyberdoc.app.ui.theme.Chart5
import java.util.Locale
import kotlin.math.roundToInt

data class MetricUi(
    val id: String,
    val title: String,
    val value: String,
    val unit: String,
    val trendLabel: String?,
    val trendUp: Boolean,
    val source: String,
    val color: Color,
    val goal: Float?,
    val weekData: List<Float>,
    val monthData: List<Float>,
)

data class GoalUi(
    val id: String,
    val metricType: MetricType,
    val metricId: String,
    val title: String,
    val current: Float,
    val target: Float,
    val unit: String,
    val color: Color,
)

data class ManualEntryDraft(
    val metricType: MetricType,
    val value: Double,
)

data class GoalDraft(
    val metricType: MetricType,
    val targetValue: Double,
)

private data class MetricPresentation(
    val id: String,
    val title: String,
    val color: Color,
)

private val dashboardMetricOrder = listOf(
    MetricType.STEPS,
    MetricType.SLEEP_DURATION,
    MetricType.EXERCISE_DURATION,
    MetricType.WEIGHT,
    MetricType.HYDRATION,
    MetricType.CALORIES_IN,
    MetricType.HEART_RATE,
)

fun editableGoalMetricTypes(): List<MetricType> = listOf(
    MetricType.STEPS,
    MetricType.SLEEP_DURATION,
    MetricType.EXERCISE_DURATION,
    MetricType.WEIGHT,
    MetricType.HYDRATION,
    MetricType.CALORIES_IN,
)

fun manualEntryMetricTypes(): List<MetricType> = listOf(
    MetricType.WEIGHT,
    MetricType.HYDRATION,
    MetricType.CALORIES_IN,
)

fun metricId(metricType: MetricType): String = metricPresentation(metricType).id

fun metricTitle(metricType: MetricType): String = metricPresentation(metricType).title

fun metricColor(metricType: MetricType): Color = metricPresentation(metricType).color

fun metricDisplayUnit(metricType: MetricType): String =
    when (metricType) {
        MetricType.STEPS -> "steps"
        MetricType.HEART_RATE -> "bpm"
        MetricType.SLEEP_DURATION -> "hours"
        MetricType.EXERCISE_DURATION -> "min"
        MetricType.HYDRATION -> "liters"
        MetricType.WEIGHT -> "kg"
        MetricType.CALORIES_IN -> "kcal"
    }

fun metricStorageUnit(metricType: MetricType): String =
    when (metricType) {
        MetricType.STEPS -> "count"
        MetricType.HEART_RATE -> "bpm"
        MetricType.SLEEP_DURATION -> "minute"
        MetricType.EXERCISE_DURATION -> "minute"
        MetricType.HYDRATION -> "ml"
        MetricType.WEIGHT -> "kg"
        MetricType.CALORIES_IN -> "kcal"
    }

fun metricInputToRawValue(metricType: MetricType, enteredValue: Double): Double =
    when (metricType) {
        MetricType.SLEEP_DURATION -> enteredValue * 60.0
        MetricType.HYDRATION -> enteredValue * 1000.0
        else -> enteredValue
    }

fun metricsData(): List<MetricUi> = dashboardMetricOrder.map(::defaultMetricUi)

fun dashboardSnapshotToMetrics(snapshot: DashboardSnapshot?): List<MetricUi> {
    if (snapshot == null) return metricsData()

    val snapshotByType = snapshot.metrics.associateBy { it.metricType }
    return dashboardMetricOrder.map { type ->
        val fallback = defaultMetricUi(type)
        val metric = snapshotByType[type] ?: return@map fallback

        fallback.copy(
            value = formatMetricValue(type, metric.value),
            unit = metricDisplayUnit(type),
            trendLabel = metric.trendPercent.takeIf { it != 0.0 }?.let { trend ->
                "${if (trend > 0) "+" else ""}${trend.roundToInt()}%"
            },
            trendUp = metric.trendPercent >= 0,
            source = when (metric.sourceId) {
                "manual" -> "Manual"
                null -> fallback.source
                else -> "Synced"
            },
            goal = metric.goalTarget?.toFloat(),
            weekData = metric.weekValues.map { chartValue(type, it).toFloat() },
            monthData = metric.monthValues.map { chartValue(type, it).toFloat() },
        )
    }
}

fun goalProgressToUi(progressList: List<GoalProgress>): List<GoalUi> =
    progressList.map { progress ->
        val type = progress.goal.metricType
        GoalUi(
            id = progress.goal.id,
            metricType = type,
            metricId = metricId(type),
            title = metricTitle(type),
            current = chartValue(type, progress.currentValue).toFloat(),
            target = chartValue(type, progress.goal.targetValue).toFloat(),
            unit = metricDisplayUnit(type),
            color = metricColor(type),
        )
    }

private fun defaultMetricUi(metricType: MetricType): MetricUi {
    val presentation = metricPresentation(metricType)
    return MetricUi(
        id = presentation.id,
        title = presentation.title,
        value = "xx",
        unit = metricDisplayUnit(metricType),
        trendLabel = null,
        trendUp = true,
        source = "Pending",
        color = presentation.color,
        goal = null,
        weekData = emptyList(),
        monthData = emptyList(),
    )
}

private fun metricPresentation(metricType: MetricType): MetricPresentation =
    when (metricType) {
        MetricType.STEPS -> MetricPresentation(
            id = "steps",
            title = "Steps",
            color = Chart1,
        )

        MetricType.HEART_RATE -> MetricPresentation(
            id = "heart",
            title = "Heart Rate",
            color = Chart4,
        )

        MetricType.SLEEP_DURATION -> MetricPresentation(
            id = "sleep",
            title = "Sleep",
            color = Chart5,
        )

        MetricType.WEIGHT -> MetricPresentation(
            id = "weight",
            title = "Weight",
            color = Chart2,
        )

        MetricType.HYDRATION -> MetricPresentation(
            id = "hydration",
            title = "Hydration",
            color = Chart3,
        )

        MetricType.CALORIES_IN -> MetricPresentation(
            id = "calories",
            title = "Calories",
            color = Chart4,
        )

        MetricType.EXERCISE_DURATION -> MetricPresentation(
            id = "activity",
            title = "Active Minutes",
            color = Chart2,
        )
    }

private fun formatMetricValue(metricType: MetricType, rawValue: Double): String {
    val displayValue = chartValue(metricType, rawValue)
    return when (metricType) {
        MetricType.STEPS,
        MetricType.HEART_RATE,
        MetricType.EXERCISE_DURATION,
        MetricType.CALORIES_IN -> displayValue.roundToInt().toString()

        MetricType.SLEEP_DURATION,
        MetricType.HYDRATION,
        MetricType.WEIGHT -> String.format(Locale.US, "%.1f", displayValue)
    }
}

private fun chartValue(metricType: MetricType, rawValue: Double): Double =
    when (metricType) {
        MetricType.SLEEP_DURATION -> rawValue / 60.0
        MetricType.HYDRATION -> rawValue / 1000.0
        else -> rawValue
    }
