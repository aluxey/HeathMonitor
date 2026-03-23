package com.cyberdoc.app.data.repository.room

import com.cyberdoc.app.data.local.dao.SyncRunDao
import com.cyberdoc.app.domain.model.SyncRun
import com.cyberdoc.app.domain.repository.SyncRepository

class RoomSyncRepository(
    private val dao: SyncRunDao,
) : SyncRepository {
    override suspend fun add(run: SyncRun) {
        dao.upsert(run.toEntity())
    }

    override suspend fun latest(limit: Int): List<SyncRun> =
        dao.latest(limit).map { it.toDomain() }
}
