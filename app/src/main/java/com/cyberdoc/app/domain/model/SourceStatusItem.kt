package com.cyberdoc.app.domain.model

data class SourceStatusItem(
    val displayName: String,
    val status: SourceStatus,
    val priority: Int,
    val lastSyncLabel: String,
    val lastError: String?,
)
