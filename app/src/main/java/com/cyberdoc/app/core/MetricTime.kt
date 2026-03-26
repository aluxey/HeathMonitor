package com.cyberdoc.app.core

import com.cyberdoc.app.domain.model.MetricType
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

fun metricLocalDate(
    metricType: MetricType,
    startAt: Instant,
    endAt: Instant,
    zoneId: ZoneId = ZoneId.systemDefault(),
): LocalDate =
    when (metricType) {
        MetricType.SLEEP_DURATION -> endAt.atZone(zoneId).toLocalDate()
        else -> startAt.atZone(zoneId).toLocalDate()
    }

fun dayRange(
    day: LocalDate,
    zoneId: ZoneId = ZoneId.systemDefault(),
): Pair<Long, Long> {
    val from = day.atStartOfDay(zoneId).toInstant().toEpochMilli()
    val to = day.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
    return from to to
}
