package com.cyberdoc.app.domain.usecase

import com.cyberdoc.app.domain.model.MetricType
import com.cyberdoc.app.domain.repository.MetricSourceSettingsRepository

class SetMetricSourcePreferenceUseCase(
    private val repository: MetricSourceSettingsRepository,
) {
    suspend operator fun invoke(metricType: MetricType, sourceId: String?) {
        repository.setPreferredSource(metricType = metricType, sourceId = sourceId)
    }
}
