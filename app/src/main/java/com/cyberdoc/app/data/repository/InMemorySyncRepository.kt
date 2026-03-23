package com.cyberdoc.app.data.repository

import com.cyberdoc.app.data.inmemory.InMemoryStore
import com.cyberdoc.app.domain.model.SyncRun
import com.cyberdoc.app.domain.repository.SyncRepository

class InMemorySyncRepository(
    private val store: InMemoryStore,
) : SyncRepository {
    override suspend fun add(run: SyncRun) {
        store.syncRuns += run
    }

    override suspend fun latest(limit: Int): List<SyncRun> =
        store.syncRuns
            .asReversed()
            .take(limit)
}
