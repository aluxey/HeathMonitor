package com.cyberdoc.app.domain.repository

import com.cyberdoc.app.domain.model.DashboardSnapshot

interface DashboardRepository {
    suspend fun snapshot(): DashboardSnapshot
}
