package com.cyberdoc.app.domain.usecase

import com.cyberdoc.app.domain.model.SyncRun
import com.cyberdoc.app.domain.repository.SyncRepository

class TriggerSyncUseCase(
    private val syncRepository: SyncRepository,
) {
    suspend operator fun invoke(run: SyncRun) {
        syncRepository.add(run)
    }
}
