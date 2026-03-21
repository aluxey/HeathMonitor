package com.cyberdoc.app.domain.repository

import com.cyberdoc.app.domain.model.SourceStatusItem
import kotlinx.coroutines.flow.Flow

interface SourceRepository {
    fun observeSources(): Flow<List<SourceStatusItem>>
}
