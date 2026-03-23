package com.cyberdoc.app.ui.figma.model

import androidx.compose.ui.graphics.Color
import com.cyberdoc.app.domain.model.DashboardSnapshot
import com.cyberdoc.app.domain.model.MetricType
import com.cyberdoc.app.ui.theme.Chart1
import com.cyberdoc.app.ui.theme.Chart2
import com.cyberdoc.app.ui.theme.Chart3
import com.cyberdoc.app.ui.theme.Chart4
import com.cyberdoc.app.ui.theme.Chart5
import java.util.Locale

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

fun metricsData(): List<MetricUi> = listOf(
    MetricUi(
        id = "steps",
        title = "Steps",
        value = "8,547",
        unit = "steps",
        trendLabel = "+12%",
        trendUp = true,
        source = "Synced",
        color = Chart1,
        goal = 10000f,
        weekData = listOf(7234f, 8912f, 9456f, 8123f, 10234f, 9876f, 8547f),
        monthData = listOf(7234f, 8912f, 9456f, 8123f, 10234f, 9876f, 8547f),
    ),
    MetricUi(
        id = "heart",
        title = "Heart Rate",
        value = "72",
        unit = "bpm",
        trendLabel = "-3%",
        trendUp = false,
        source = "Synced",
        color = Chart4,
        goal = null,
        weekData = listOf(74f, 73f, 75f, 71f, 72f, 70f, 72f),
        monthData = listOf(74f, 73f, 75f, 71f, 72f, 70f, 72f),
    ),
    MetricUi(
        id = "sleep",
        title = "Sleep",
        value = "7.5",
        unit = "hours",
        trendLabel = "+8%",
        trendUp = true,
        source = "Synced",
        color = Chart5,
        goal = 8f,
        weekData = listOf(7.2f, 6.8f, 7.5f, 8.1f, 7.0f, 8.5f, 7.5f),
        monthData = listOf(7.2f, 6.8f, 7.5f, 8.1f, 7.0f, 8.5f, 7.5f),
    ),
    MetricUi(
        id = "activity",
        title = "Active Minutes",
        value = "45",
        unit = "min",
        trendLabel = "+15%",
        trendUp = true,
        source = "Synced",
        color = Chart2,
        goal = 60f,
        weekData = listOf(35f, 42f, 50f, 38f, 55f, 60f, 45f),
        monthData = listOf(35f, 42f, 50f, 38f, 55f, 60f, 45f),
    ),
    MetricUi(
        id = "hydration",
        title = "Hydration",
        value = "1.8",
        unit = "liters",
        trendLabel = null,
        trendUp = true,
        source = "Manual",
        color = Chart3,
        goal = 2.5f,
        weekData = listOf(2.1f, 1.9f, 2.3f, 1.7f, 2.0f, 2.4f, 1.8f),
        monthData = listOf(2.1f, 1.9f, 2.3f, 1.7f, 2.0f, 2.4f, 1.8f),
    ),
)

fun dashboardSnapshotToMetrics(snapshot: DashboardSnapshot?): List<MetricUi> {
    if (snapshot == null) return metricsData()

    val base = listOf(
        baseMetric(MetricType.STEPS),
        baseMetric(MetricType.HEART_RATE),
        baseMetric(MetricType.SLEEP_DURATION),
        baseMetric(MetricType.WEIGHT),
        baseMetric(MetricType.HYDRATION),
        baseMetric(MetricType.CALORIES_IN),
    )

    val byType = snapshot.metrics.associateBy { it.metricType }
    return base.map { item ->
        val data = byType[item.metricType] ?: return@map item.fallback
        val value = when (item.metricType) {
            MetricType.STEPS -> data.value.toInt().toString()
            MetricType.SLEEP_DURATION -> String.format(Locale.US, "%.1f", data.value / 60.0)
            MetricType.HYDRATION -> String.format(Locale.US, "%.1f", data.value / 1000.0)
            MetricType.WEIGHT -> String.format(Locale.US, "%.1f", data.value)
            MetricType.CALORIES_IN -> data.value.toInt().toString()
            MetricType.HEART_RATE -> data.value.toInt().toString()
            MetricType.EXERCISE_DURATION -> data.value.toInt().toString()
        }

        item.fallback.copy(
            value = value,
            unit = item.displayUnit,
            trendLabel = if (data.trendPercent == 0.0) null else "${if (data.trendPercent > 0) "+" else ""}${data.trendPercent.toInt()}%",
            trendUp = data.trendPercent >= 0,
            source = "Synced",
        )
    }
}

private data class MetricTemplate(
    val metricType: MetricType,
    val displayUnit: String,
    val fallback: MetricUi,
)

private fun baseMetric(type: MetricType): MetricTemplate =
    when (type) {
        MetricType.STEPS -> MetricTemplate(
            metricType = type,
            displayUnit = "steps",
            fallback = MetricUi(
                id = "steps",
                title = "Steps",
                value = "--",
                unit = "steps",
                trendLabel = null,
                trendUp = true,
                source = "Pending",
                color = Chart1,
                goal = 10000f,
                weekData = listOf(0f),
                monthData = listOf(0f),
            ),
        )

        MetricType.HEART_RATE -> MetricTemplate(
            metricType = type,
            displayUnit = "bpm",
            fallback = MetricUi(
                id = "heart",
                title = "Heart Rate",
                value = "--",
                unit = "bpm",
                trendLabel = null,
                trendUp = true,
                source = "Pending",
                color = Chart4,
                goal = null,
                weekData = listOf(0f),
                monthData = listOf(0f),
            ),
        )

        MetricType.SLEEP_DURATION -> MetricTemplate(
            metricType = type,
            displayUnit = "hours",
            fallback = MetricUi(
                id = "sleep",
                title = "Sleep",
                value = "--",
                unit = "hours",
                trendLabel = null,
                trendUp = true,
                source = "Pending",
                color = Chart5,
                goal = 8f,
                weekData = listOf(0f),
                monthData = listOf(0f),
            ),
        )

        MetricType.WEIGHT -> MetricTemplate(
            metricType = type,
            displayUnit = "kg",
            fallback = MetricUi(
                id = "weight",
                title = "Weight",
                value = "--",
                unit = "kg",
                trendLabel = null,
                trendUp = true,
                source = "Pending",
                color = Chart2,
                goal = null,
                weekData = listOf(0f),
                monthData = listOf(0f),
            ),
        )

        MetricType.HYDRATION -> MetricTemplate(
            metricType = type,
            displayUnit = "liters",
            fallback = MetricUi(
                id = "hydration",
                title = "Hydration",
                value = "--",
                unit = "liters",
                trendLabel = null,
                trendUp = true,
                source = "Pending",
                color = Chart3,
                goal = 2.5f,
                weekData = listOf(0f),
                monthData = listOf(0f),
            ),
        )

        MetricType.CALORIES_IN -> MetricTemplate(
            metricType = type,
            displayUnit = "kcal",
            fallback = MetricUi(
                id = "calories",
                title = "Calories",
                value = "--",
                unit = "kcal",
                trendLabel = null,
                trendUp = true,
                source = "Pending",
                color = Chart2,
                goal = null,
                weekData = listOf(0f),
                monthData = listOf(0f),
            ),
        )

        MetricType.EXERCISE_DURATION -> MetricTemplate(
            metricType = type,
            displayUnit = "min",
            fallback = MetricUi(
                id = "activity",
                title = "Active Minutes",
                value = "--",
                unit = "min",
                trendLabel = null,
                trendUp = true,
                source = "Pending",
                color = Chart2,
                goal = 60f,
                weekData = listOf(0f),
                monthData = listOf(0f),
            ),
        )
    }
