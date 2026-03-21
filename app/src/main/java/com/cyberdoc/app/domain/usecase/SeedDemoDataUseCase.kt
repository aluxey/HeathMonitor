package com.cyberdoc.app.domain.usecase

import androidx.room.withTransaction
import com.cyberdoc.app.data.local.CyberDocDatabase
import com.cyberdoc.app.data.local.entity.DailyAggregateEntity
import com.cyberdoc.app.data.local.entity.DataSourceEntity
import com.cyberdoc.app.data.local.entity.GoalEntity
import com.cyberdoc.app.data.local.entity.SyncRunEntity
import com.cyberdoc.app.domain.model.MetricType
import com.cyberdoc.app.domain.model.PeriodType
import com.cyberdoc.app.domain.model.QualityFlag
import com.cyberdoc.app.domain.model.SourceStatus
import com.cyberdoc.app.domain.model.SourceType
import com.cyberdoc.app.domain.model.SyncStatus
import java.time.LocalDate
import java.util.UUID

class SeedDemoDataUseCase(
    private val database: CyberDocDatabase,
) {
    suspend operator fun invoke() {
        val sourceDao = database.dataSourceDao()
        if (sourceDao.getById(HEALTH_CONNECT_SOURCE_ID) != null) {
            return
        }

        val now = System.currentTimeMillis()
        val today = LocalDate.now().toString()

        database.withTransaction {
            database.dataSourceDao().upsertAll(
                listOf(
                    DataSourceEntity(
                        id = HEALTH_CONNECT_SOURCE_ID,
                        type = SourceType.HEALTH_CONNECT,
                        displayName = "Health Connect",
                        status = SourceStatus.NEEDS_PERMISSION,
                        priority = 1,
                        lastSyncAt = null,
                        lastError = null,
                    ),
                    DataSourceEntity(
                        id = MANUAL_SOURCE_ID,
                        type = SourceType.MANUAL,
                        displayName = "Saisie manuelle",
                        status = SourceStatus.CONNECTED,
                        priority = 2,
                        lastSyncAt = now,
                        lastError = null,
                    ),
                ),
            )

            database.goalDao().upsertAll(
                listOf(
                    GoalEntity("goal_steps", MetricType.STEPS, 8000.0, PeriodType.DAILY, today, null, true),
                    GoalEntity("goal_sleep", MetricType.SLEEP_DURATION, 8.0, PeriodType.DAILY, today, null, true),
                    GoalEntity("goal_weight", MetricType.WEIGHT, 77.0, PeriodType.DAILY, today, null, true),
                    GoalEntity("goal_calories", MetricType.CALORIES_IN, 2300.0, PeriodType.DAILY, today, null, true),
                    GoalEntity("goal_exercise", MetricType.EXERCISE_DURATION, 45.0, PeriodType.DAILY, today, null, true),
                ),
            )

            database.dailyAggregateDao().upsertAll(
                listOf(
                    aggregate("agg_steps", today, MetricType.STEPS, 6240.0, "count", HEALTH_CONNECT_SOURCE_ID, now),
                    aggregate("agg_sleep", today, MetricType.SLEEP_DURATION, 7.2, "hours", HEALTH_CONNECT_SOURCE_ID, now),
                    aggregate("agg_weight", today, MetricType.WEIGHT, 78.4, "kg", MANUAL_SOURCE_ID, now),
                    aggregate("agg_calories", today, MetricType.CALORIES_IN, 1840.0, "kcal", HEALTH_CONNECT_SOURCE_ID, now),
                    aggregate("agg_exercise", today, MetricType.EXERCISE_DURATION, 32.0, "minutes", HEALTH_CONNECT_SOURCE_ID, now),
                ),
            )

            database.syncRunDao().insert(
                SyncRunEntity(
                    id = UUID.randomUUID().toString(),
                    sourceId = MANUAL_SOURCE_ID,
                    startedAt = now,
                    endedAt = now,
                    status = SyncStatus.SUCCESS,
                    recordsRead = 1,
                    message = "Jeu de donnees initial cree pour le dashboard",
                ),
            )
        }
    }

    private fun aggregate(
        id: String,
        date: String,
        metricType: MetricType,
        value: Double,
        unit: String,
        sourceId: String,
        computedAt: Long,
    ) = DailyAggregateEntity(
        id = id,
        date = date,
        metricType = metricType,
        value = value,
        unit = unit,
        sourceId = sourceId,
        qualityFlag = QualityFlag.OK,
        computedAt = computedAt,
    )

    companion object {
        const val HEALTH_CONNECT_SOURCE_ID = "source_health_connect"
        const val MANUAL_SOURCE_ID = "source_manual"
    }
}
