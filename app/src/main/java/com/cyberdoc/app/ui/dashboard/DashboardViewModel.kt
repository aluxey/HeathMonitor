package com.cyberdoc.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberdoc.app.domain.model.TrendRange
import com.cyberdoc.app.domain.repository.DashboardRepository
import com.cyberdoc.app.domain.usecase.SaveManualWeightUseCase
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardUiState(
    val dateLabel: String = LocalDate.now().toString(),
    val selectedRange: TrendRange = TrendRange.DAYS_7,
    val metrics: List<com.cyberdoc.app.domain.model.DashboardMetric> = emptyList(),
    val isSavingWeight: Boolean = false,
)

class DashboardViewModel(
    private val dashboardRepository: DashboardRepository,
    private val saveManualWeightUseCase: SaveManualWeightUseCase,
) : ViewModel() {
    private val selectedRange = MutableStateFlow(TrendRange.DAYS_7)
    private val isSavingWeight = MutableStateFlow(false)

    val uiState: StateFlow<DashboardUiState> =
        combine(
            selectedRange,
            isSavingWeight,
            selectedRange.flatMapLatest { range ->
                dashboardRepository.observeDashboard(LocalDate.now(), range.days)
            },
        ) { range, savingWeight, metrics ->
            DashboardUiState(
                dateLabel = LocalDate.now().toString(),
                selectedRange = range,
                metrics = metrics,
                isSavingWeight = savingWeight,
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = DashboardUiState(),
            )

    fun selectRange(range: TrendRange) {
        selectedRange.value = range
    }

    fun saveManualWeight(input: String): Boolean {
        val weightKg = input.replace(',', '.').toDoubleOrNull()
        if (weightKg == null || weightKg <= 0.0) {
            return false
        }

        viewModelScope.launch {
            isSavingWeight.value = true
            try {
                saveManualWeightUseCase(weightKg)
            } finally {
                isSavingWeight.value = false
            }
        }
        return true
    }
}
