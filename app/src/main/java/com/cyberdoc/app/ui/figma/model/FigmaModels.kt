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
    val fallbackValue: String,
    val fallbackGoal: Float?,
    val fallbackWeekData: List<Float>,
    val fallbackMonthData: List<Float>,
)

private val dashboardMetricOrder = listOf(
    MetricType.STEPS,
    MetricType.HEART_RATE,
    MetricType.SLEEP_DURATION,
    MetricType.WEIGHT,
    MetricType.HYDRATION,
    MetricType.CALORIES_IN,
)

fun editableGoalMetricTypes(): List<MetricType> = listOf(
    MetricType.STEPS,
    MetricType.SLEEP_DURATION,
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
    if (snapshot.metrics.isEmpty()) return emptyList()

    val snapshotByType = snapshot.metrics.associateBy { it.metricType }
    return dashboardMetricOrder.mapNotNull { type ->
        val metric = snapshotByType[type] ?: return@mapNotNull null
        val fallback = defaultMetricUi(type)

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
        value = presentation.fallbackValue,
        unit = metricDisplayUnit(metricType),
        trendLabel = null,
        trendUp = true,
        source = if (metricType == MetricType.HYDRATION || metricType == MetricType.WEIGHT) {
            "Manual"
        } else {
            "Synced"
        },
        color = presentation.color,
        goal = presentation.fallbackGoal,
        weekData = presentation.fallbackWeekData,
        monthData = presentation.fallbackMonthData,
    )
}

private fun metricPresentation(metricType: MetricType): MetricPresentation =
    when (metricType) {
        MetricType.STEPS -> MetricPresentation(
            id = "steps",
            title = "Steps",
            color = Chart1,
            fallbackValue = "8,547",
            fallbackGoal = 10000f,
            fallbackWeekData = listOf(7234f, 8912f, 9456f, 8123f, 10234f, 9876f, 8547f),
            fallbackMonthData = listOf(7234f, 8456f, 9102f, 9754f, 8123f, 9340f, 10234f),
        )

        MetricType.HEART_RATE -> MetricPresentation(
            id = "heart",
            title = "Heart Rate",
            color = Chart4,
            fallbackValue = "72",
            fallbackGoal = null,
            fallbackWeekData = listOf(74f, 73f, 75f, 71f, 72f, 70f, 72f),
            fallbackMonthData = listOf(74f, 76f, 75f, 72f, 71f, 70f, 72f),
        )

        MetricType.SLEEP_DURATION -> MetricPresentation(
            id = "sleep",
            title = "Sleep",
            color = Chart5,
            fallbackValue = "7.5",
            fallbackGoal = 8f,
            fallbackWeekData = listOf(7.2f, 6.8f, 7.5f, 8.1f, 7.0f, 8.5f, 7.5f),
            fallbackMonthData = listOf(6.9f, 7.4f, 7.0f, 8.1f, 7.8f, 8.3f, 7.5f),
        )

        MetricType.WEIGHT -> MetricPresentation(
            id = "weight",
            title = "Weight",
            color = Chart2,
            fallbackValue = "70.4",
            fallbackGoal = 68f,
            fallbackWeekData = listOf(71.2f, 71.0f, 70.9f, 70.8f, 70.6f, 70.5f, 70.4f),
            fallbackMonthData = listOf(72.0f, 71.8f, 71.5f, 71.2f, 71.0f, 70.7f, 70.4f),
        )

        MetricType.HYDRATION -> MetricPresentation(
            id = "hydration",
            title = "Hydration",
            color = Chart3,
            fallbackValue = "1.8",
            fallbackGoal = 2.5f,
            fallbackWeekData = listOf(2.1f, 1.9f, 2.3f, 1.7f, 2.0f, 2.4f, 1.8f),
            fallbackMonthData = listOf(1.8f, 2.1f, 2.2f, 2.0f, 1.9f, 2.4f, 1.8f),
        )

        MetricType.CALORIES_IN -> MetricPresentation(
            id = "calories",
            title = "Calories",
            color = Chart4,
            fallbackValue = "1820",
            fallbackGoal = 2100f,
            fallbackWeekData = listOf(1750f, 1890f, 1960f, 1810f, 2050f, 1920f, 1820f),
            fallbackMonthData = listOf(1700f, 1840f, 1910f, 2000f, 1880f, 1940f, 1820f),
        )

        MetricType.EXERCISE_DURATION -> MetricPresentation(
            id = "activity",
            title = "Active Minutes",
            color = Chart2,
            fallbackValue = "45",
            fallbackGoal = 60f,
            fallbackWeekData = listOf(35f, 42f, 50f, 38f, 55f, 60f, 45f),
            fallbackMonthData = listOf(30f, 38f, 47f, 54f, 40f, 60f, 45f),
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
