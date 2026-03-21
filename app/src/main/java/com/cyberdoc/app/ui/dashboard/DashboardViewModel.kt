package com.cyberdoc.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberdoc.app.domain.model.DashboardMetric
import com.cyberdoc.app.domain.repository.DashboardRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class DashboardUiState(
    val dateLabel: String = LocalDate.now().toString(),
    val metrics: List<DashboardMetric> = emptyList(),
)

class DashboardViewModel(
    dashboardRepository: DashboardRepository,
) : ViewModel() {
    val uiState: StateFlow<DashboardUiState> =
        dashboardRepository.observeToday(LocalDate.now())
            .map { metrics ->
                DashboardUiState(
                    dateLabel = LocalDate.now().toString(),
                    metrics = metrics,
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = DashboardUiState(),
            )
}
