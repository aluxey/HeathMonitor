package com.cyberdoc.app.ui.sources

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberdoc.app.domain.model.HealthConnectAvailability
import com.cyberdoc.app.domain.model.SourceStatusItem
import com.cyberdoc.app.domain.model.SyncStatus
import com.cyberdoc.app.domain.repository.SourceRepository
import com.cyberdoc.app.integration.healthconnect.HealthConnectManager
import com.cyberdoc.app.domain.usecase.SyncHealthDataUseCase
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
    val isSyncing: Boolean = false,
    val lastSyncStatus: SyncStatus? = null,
    val lastSyncMessage: String? = null,
    val lastSyncRecordsRead: Int? = null,
)

class SourcesViewModel(
    private val sourceRepository: SourceRepository,
    private val healthConnectManager: HealthConnectManager,
    private val syncHealthDataUseCase: SyncHealthDataUseCase,
) : ViewModel() {
    private val permissionState = MutableStateFlow(
        PermissionSnapshot(
            availability = healthConnectManager.availability(),
            grantedPermissionsCount = 0,
            requiredPermissionsCount = healthConnectManager.requiredPermissions().size,
            allPermissionsGranted = false,
        ),
    )
    private val syncState = MutableStateFlow(SyncSnapshot())

    val uiState: StateFlow<SourcesUiState> =
        combine(
            sourceRepository.observeSources(),
            permissionState,
            syncState,
        ) { sources, permissions, sync ->
            SourcesUiState(
                availability = permissions.availability,
                grantedPermissionsCount = permissions.grantedPermissionsCount,
                requiredPermissionsCount = permissions.requiredPermissionsCount,
                allPermissionsGranted = permissions.allPermissionsGranted,
                sources = sources,
                isSyncing = sync.isSyncing,
                lastSyncStatus = sync.lastSyncStatus,
                lastSyncMessage = sync.lastSyncMessage,
                lastSyncRecordsRead = sync.lastSyncRecordsRead,
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

    fun syncNow() {
        viewModelScope.launch {
            syncState.value = syncState.value.copy(
                isSyncing = true,
                lastSyncMessage = null,
                lastSyncStatus = null,
                lastSyncRecordsRead = null,
            )
            try {
                val result = syncHealthDataUseCase()
                syncState.value = SyncSnapshot(
                    isSyncing = false,
                    lastSyncStatus = result.status,
                    lastSyncMessage = result.message,
                    lastSyncRecordsRead = result.recordsRead,
                )
            } catch (_: Exception) {
                syncState.value = SyncSnapshot(
                    isSyncing = false,
                    lastSyncStatus = SyncStatus.FAILURE,
                    lastSyncMessage = "Echec de la synchro. Verifie Health Connect et les permissions.",
                    lastSyncRecordsRead = null,
                )
            } finally {
                refreshPermissions()
            }
        }
    }

    private data class PermissionSnapshot(
        val availability: HealthConnectAvailability,
        val grantedPermissionsCount: Int,
        val requiredPermissionsCount: Int,
        val allPermissionsGranted: Boolean,
    )

    private data class SyncSnapshot(
        val isSyncing: Boolean = false,
        val lastSyncStatus: SyncStatus? = null,
        val lastSyncMessage: String? = null,
        val lastSyncRecordsRead: Int? = null,
    )
}
