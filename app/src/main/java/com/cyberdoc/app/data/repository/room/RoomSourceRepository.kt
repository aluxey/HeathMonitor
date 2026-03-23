package com.cyberdoc.app.data.repository.room

import com.cyberdoc.app.data.local.dao.DataSourceDao
import com.cyberdoc.app.domain.model.DataSource
import com.cyberdoc.app.domain.repository.SourceRepository

class RoomSourceRepository(
    private val dao: DataSourceDao,
) : SourceRepository {
    override suspend fun all(): List<DataSource> =
        dao.all().map { it.toDomain() }

    override suspend fun upsert(source: DataSource) {
        dao.upsert(source.toEntity())
    }

    override suspend fun byId(id: String): DataSource? =
        dao.byId(id)?.toDomain()
}
