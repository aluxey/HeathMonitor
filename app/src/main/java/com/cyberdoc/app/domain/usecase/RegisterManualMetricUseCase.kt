package com.cyberdoc.app.domain.usecase

import com.cyberdoc.app.core.AppResult
import com.cyberdoc.app.core.ValidationError
import com.cyberdoc.app.domain.model.MetricRecord
import com.cyberdoc.app.domain.repository.MetricRepository

class RegisterManualMetricUseCase(
    private val metricRepository: MetricRepository,
) {
    suspend operator fun invoke(record: MetricRecord): AppResult<Unit> {
        if (record.value < 0.0) {
            return AppResult.Failure(ValidationError("Metric value cannot be negative"))
        }
        metricRepository.add(record)
        return AppResult.Success(Unit)
    }
}
