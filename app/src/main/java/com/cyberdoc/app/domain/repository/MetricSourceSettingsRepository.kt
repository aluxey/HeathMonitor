package com.cyberdoc.app.domain.repository

import com.cyberdoc.app.domain.model.MetricSourceSetting
import com.cyberdoc.app.domain.model.MetricType

interface MetricSourceSettingsRepository {
    suspend fun allSettings(): List<MetricSourceSetting>
    suspend fun setting(metricType: MetricType): MetricSourceSetting
    suspend fun setPreferredSource(metricType: MetricType, sourceId: String?)
}
