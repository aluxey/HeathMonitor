package com.cyberdoc.app.data.inmemory

import com.cyberdoc.app.domain.model.DataSource
import com.cyberdoc.app.domain.model.Goal
import com.cyberdoc.app.domain.model.MetricRecord
import com.cyberdoc.app.domain.model.SyncRun

class InMemoryStore {
    val sources = mutableListOf<DataSource>()
    val goals = mutableListOf<Goal>()
    val metrics = mutableListOf<MetricRecord>()
    val syncRuns = mutableListOf<SyncRun>()
}
