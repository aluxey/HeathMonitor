package com.cyberdoc.app.domain.usecase

import com.cyberdoc.app.domain.model.MetricSourceSetting
import com.cyberdoc.app.domain.repository.MetricSourceSettingsRepository

class GetMetricSourceSettingsUseCase(
    private val repository: MetricSourceSettingsRepository,
) {
    suspend operator fun invoke(): List<MetricSourceSetting> = repository.allSettings()
}
