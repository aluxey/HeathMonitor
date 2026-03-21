package com.cyberdoc.app.ui.sources

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberdoc.app.domain.model.HealthConnectAvailability
import com.cyberdoc.app.domain.model.SourceStatusItem
import com.cyberdoc.app.domain.repository.SourceRepository
import com.cyberdoc.app.integration.healthconnect.HealthConnectManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SourcesUiState(
    val availability: HealthConnectAvailability = HealthConnectAvailability.NOT_SUPPORTED,
    val grantedPermissionsCount: Int = 0,
    val requiredPermissionsCount: Int = 0,
    val allPermissionsGranted: Boolean = false,
    val sources: List<SourceStatusItem> = emptyList(),
)

class SourcesViewModel(
    private val sourceRepository: SourceRepository,
    private val healthConnectManager: HealthConnectManager,
) : ViewModel() {
    private val permissionState = MutableStateFlow(
        PermissionSnapshot(
            availability = healthConnectManager.availability(),
            grantedPermissionsCount = 0,
            requiredPermissionsCount = healthConnectManager.requiredPermissions().size,
            allPermissionsGranted = false,
        ),
    )

    val uiState: StateFlow<SourcesUiState> =
        combine(
            sourceRepository.observeSources(),
            permissionState,
        ) { sources, permissions ->
            SourcesUiState(
                availability = permissions.availability,
                grantedPermissionsCount = permissions.grantedPermissionsCount,
                requiredPermissionsCount = permissions.requiredPermissionsCount,
                allPermissionsGranted = permissions.allPermissionsGranted,
                sources = sources,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SourcesUiState(),
        )

    init {
        refreshPermissions()
    }

    fun requiredPermissions(): Set<String> = healthConnectManager.requiredPermissions()

    fun refreshPermissions() {
        viewModelScope.launch {
            val granted = healthConnectManager.grantedPermissions()
            permissionState.value = PermissionSnapshot(
                availability = healthConnectManager.availability(),
                grantedPermissionsCount = granted.size,
                requiredPermissionsCount = healthConnectManager.requiredPermissions().size,
                allPermissionsGranted = healthConnectManager.hasAllPermissions(granted),
            )
        }
    }

    private data class PermissionSnapshot(
        val availability: HealthConnectAvailability,
        val grantedPermissionsCount: Int,
        val requiredPermissionsCount: Int,
        val allPermissionsGranted: Boolean,
    )
}
