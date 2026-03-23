package com.cyberdoc.app.domain.repository

import com.cyberdoc.app.domain.model.SyncRun

interface SyncRepository {
    suspend fun add(run: SyncRun)
    suspend fun latest(limit: Int = 20): List<SyncRun>
}
