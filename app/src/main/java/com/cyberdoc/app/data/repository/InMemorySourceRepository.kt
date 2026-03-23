package com.cyberdoc.app.data.repository

import com.cyberdoc.app.data.inmemory.InMemoryStore
import com.cyberdoc.app.domain.model.DataSource
import com.cyberdoc.app.domain.repository.SourceRepository

class InMemorySourceRepository(
    private val store: InMemoryStore,
) : SourceRepository {
    override suspend fun all(): List<DataSource> =
        store.sources.sortedBy { it.priority }

    override suspend fun upsert(source: DataSource) {
        val index = store.sources.indexOfFirst { it.id == source.id }
        if (index == -1) {
            store.sources += source
        } else {
            store.sources[index] = source
        }
    }

    override suspend fun byId(id: String): DataSource? =
        store.sources.firstOrNull { it.id == id }
}
