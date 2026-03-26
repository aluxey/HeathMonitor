package com.cyberdoc.app.domain.service

import com.cyberdoc.app.domain.model.MetricRecord
import com.cyberdoc.app.domain.model.MetricType
import com.cyberdoc.app.integration.healthconnect.HealthConnectRawRecord
import com.cyberdoc.app.integration.healthconnect.HealthDataType
import java.time.Instant
import java.util.UUID

interface MetricsNormalizer {
    fun normalize(records: List<HealthConnectRawRecord>, importedAt: Instant, fallbackSourceId: String): List<MetricRecord>
}

class DefaultMetricsNormalizer : MetricsNormalizer {
    override fun normalize(
        records: List<HealthConnectRawRecord>,
        importedAt: Instant,
        fallbackSourceId: String,
    ): List<MetricRecord> =
        records.mapNotNull { raw ->
            val metricType = raw.dataType.toMetricType() ?: return@mapNotNull null
            val normalizedUnit = normalizeUnit(metricType = metricType, unit = raw.unit)
            val normalizedValue = normalizeValue(metricType = metricType, value = raw.value, unit = raw.unit)
            val resolvedSourceId = raw.sourceAppId?.takeIf { it.isNotBlank() } ?: fallbackSourceId

            MetricRecord(
                id = UUID.randomUUID().toString(),
                metricType = metricType,
                value = normalizedValue,
                unit = normalizedUnit,
                startAt = raw.startAt,
                endAt = raw.endAt,
                sourceId = resolvedSourceId,
                externalId = raw.externalId ?: fallbackExternalId(
                    raw = raw,
                    metricType = metricType,
                    sourceId = resolvedSourceId,
                    normalizedValue = normalizedValue,
                    normalizedUnit = normalizedUnit,
                ),
                isManual = false,
                createdAt = importedAt,
            )
        }

    private fun normalizeUnit(metricType: MetricType, unit: String): String =
        when (metricType) {
            MetricType.STEPS -> "count"
            MetricType.SLEEP_DURATION -> "minute"
            MetricType.WEIGHT -> "kg"
            MetricType.HYDRATION -> "ml"
            MetricType.CALORIES_IN -> "kcal"
            MetricType.HEART_RATE -> "bpm"
            MetricType.EXERCISE_DURATION -> "minute"
        }

    private fun normalizeValue(metricType: MetricType, value: Double, unit: String): Double =
        when (metricType) {
            MetricType.SLEEP_DURATION,
            MetricType.EXERCISE_DURATION -> {
                if (unit.equals("hour", ignoreCase = true) || unit.equals("hours", ignoreCase = true)) {
                    value * 60.0
                } else {
                    value
                }
            }

            MetricType.HYDRATION -> {
                if (unit.equals("l", ignoreCase = true) || unit.equals("liter", ignoreCase = true)) {
                    value * 1000.0
                } else {
                    value
                }
            }

            else -> value
        }

    private fun fallbackExternalId(
        raw: HealthConnectRawRecord,
        metricType: MetricType,
        sourceId: String,
        normalizedValue: Double,
        normalizedUnit: String,
    ): String =
        listOf(
            "health-connect",
            metricType.name,
            sourceId,
            raw.startAt.toEpochMilli().toString(),
            raw.endAt.toEpochMilli().toString(),
            normalizedValue.toString(),
            normalizedUnit,
        ).joinToString(separator = ":")

    private fun HealthDataType.toMetricType(): MetricType? =
        when (this) {
            HealthDataType.STEPS -> MetricType.STEPS
            HealthDataType.SLEEP -> MetricType.SLEEP_DURATION
            HealthDataType.EXERCISE -> MetricType.EXERCISE_DURATION
            HealthDataType.WEIGHT -> MetricType.WEIGHT
            HealthDataType.HYDRATION -> MetricType.HYDRATION
            HealthDataType.CALORIES_IN -> MetricType.CALORIES_IN
            HealthDataType.HEART_RATE -> MetricType.HEART_RATE
        }
}
