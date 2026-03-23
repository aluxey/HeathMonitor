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
        monthData = listOf(7234f, 8456f, 9102f, 9754f, 8123f, 9340f, 10234f),
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
        monthData = listOf(74f, 76f, 75f, 72f, 71f, 70f, 72f),
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
        monthData = listOf(6.9f, 7.4f, 7.0f, 8.1f, 7.8f, 8.3f, 7.5f),
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
        monthData = listOf(30f, 38f, 47f, 54f, 40f, 60f, 45f),
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
        monthData = listOf(1.8f, 2.1f, 2.2f, 2.0f, 1.9f, 2.4f, 1.8f),
    ),
)

fun dashboardSnapshotToMetrics(snapshot: DashboardSnapshot?): List<MetricUi> {
    if (snapshot == null) return metricsData()

    val fallbackByType = listOf(
        MetricType.STEPS to metricsData().first { it.id == "steps" },
        MetricType.HEART_RATE to metricsData().first { it.id == "heart" },
        MetricType.SLEEP_DURATION to metricsData().first { it.id == "sleep" },
        MetricType.EXERCISE_DURATION to metricsData().first { it.id == "activity" },
        MetricType.HYDRATION to metricsData().first { it.id == "hydration" },
    )

    val snapshotByType = snapshot.metrics.associateBy { it.metricType }
    return fallbackByType.map { (type, fallback) ->
        val metric = snapshotByType[type] ?: return@map fallback.copy(source = fallback.source)
        fallback.copy(
            value = formatSnapshotValue(type, metric.value),
            unit = displayUnit(type),
            trendLabel = metric.trendPercent.takeIf { it != 0.0 }?.let { trend ->
                "${if (trend > 0) "+" else ""}${trend.toInt()}%"
            },
            trendUp = metric.trendPercent >= 0,
            source = if (type == MetricType.HYDRATION) "Manual" else "Synced",
        )
    }
}

private fun displayUnit(metricType: MetricType): String =
    when (metricType) {
        MetricType.STEPS -> "steps"
        MetricType.HEART_RATE -> "bpm"
        MetricType.SLEEP_DURATION -> "hours"
        MetricType.EXERCISE_DURATION -> "min"
        MetricType.HYDRATION -> "liters"
        MetricType.WEIGHT -> "kg"
        MetricType.CALORIES_IN -> "kcal"
    }

private fun formatSnapshotValue(metricType: MetricType, rawValue: Double): String =
    when (metricType) {
        MetricType.STEPS,
        MetricType.HEART_RATE,
        MetricType.EXERCISE_DURATION,
        MetricType.CALORIES_IN -> rawValue.toInt().toString()

        MetricType.SLEEP_DURATION -> String.format(Locale.US, "%.1f", rawValue / 60.0)
        MetricType.HYDRATION -> String.format(Locale.US, "%.1f", rawValue / 1000.0)
        MetricType.WEIGHT -> String.format(Locale.US, "%.1f", rawValue)
    }
