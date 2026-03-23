package com.cyberdoc.app.domain.repository

import com.cyberdoc.app.domain.model.DataSource

interface SourceRepository {
    suspend fun all(): List<DataSource>
    suspend fun upsert(source: DataSource)
    suspend fun byId(id: String): DataSource?
}
