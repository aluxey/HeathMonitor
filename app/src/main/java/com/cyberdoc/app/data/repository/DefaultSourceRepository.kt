package com.cyberdoc.app.data.repository

import com.cyberdoc.app.data.local.dao.DataSourceDao
import com.cyberdoc.app.data.local.dao.SyncRunDao
import com.cyberdoc.app.domain.model.SourceStatusItem
import com.cyberdoc.app.domain.repository.SourceRepository
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class DefaultSourceRepository(
    private val sourceDao: DataSourceDao,
    private val syncRunDao: SyncRunDao,
) : SourceRepository {
    override fun observeSources(): Flow<List<SourceStatusItem>> =
        combine(
            sourceDao.observeAll(),
            syncRunDao.observeAll(),
        ) { sources, syncRuns ->
            sources.map { source ->
                val latestRun = syncRuns.firstOrNull { it.sourceId == source.id }
                SourceStatusItem(
                    displayName = source.displayName,
                    status = source.status,
                    priority = source.priority,
                    lastSyncLabel = source.lastSyncAt?.let(::formatDateTime)
                        ?: latestRun?.endedAt?.let(::formatDateTime)
                        ?: "Aucune synchro",
                    lastError = source.lastError ?: latestRun?.message,
                )
            }
        }

    private fun formatDateTime(timestamp: Long): String =
        Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
            .format(DateTimeFormatter.ofPattern("dd/MM HH:mm"))
}
