package com.cyberdoc.app.ui.figma.model

import androidx.compose.ui.graphics.Color
import com.cyberdoc.app.ui.theme.Chart1
import com.cyberdoc.app.ui.theme.Chart2
import com.cyberdoc.app.ui.theme.Chart3
import com.cyberdoc.app.ui.theme.Chart4
import com.cyberdoc.app.ui.theme.Chart5

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
