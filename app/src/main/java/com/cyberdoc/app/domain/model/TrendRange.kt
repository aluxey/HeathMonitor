package com.cyberdoc.app.domain.model

enum class TrendRange(
    val days: Int,
    val label: String,
) {
    DAYS_7(7, "7 jours"),
    DAYS_30(30, "30 jours"),
}
