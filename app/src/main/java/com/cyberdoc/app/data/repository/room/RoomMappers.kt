package com.cyberdoc.app.data.repository.room

import com.cyberdoc.app.data.local.entity.DailyAggregateEntity
import com.cyberdoc.app.data.local.entity.DataSourceEntity
import com.cyberdoc.app.data.local.entity.GoalEntity
import com.cyberdoc.app.data.local.entity.MetricRecordEntity
import com.cyberdoc.app.data.local.entity.SyncRunEntity
import com.cyberdoc.app.domain.model.DailyAggregate
import com.cyberdoc.app.domain.model.DataSource
import com.cyberdoc.app.domain.model.Goal
import com.cyberdoc.app.domain.model.MetricRecord
import com.cyberdoc.app.domain.model.PeriodType
import com.cyberdoc.app.domain.model.QualityFlag
import com.cyberdoc.app.domain.model.SourceStatus
import com.cyberdoc.app.domain.model.SourceType
import com.cyberdoc.app.domain.model.SyncRun
import com.cyberdoc.app.domain.model.SyncStatus
import java.time.Instant
import java.time.LocalDate

internal fun DataSourceEntity.toDomain(): DataSource =
    DataSource(
        id = id,
        type = SourceType.valueOf(type),
        displayName = displayName,
        status = SourceStatus.valueOf(status),
        priority = priority,
        lastSyncAt = lastSyncAtEpochMillis?.let(Instant::ofEpochMilli),
        lastError = lastError,
    )

internal fun DataSource.toEntity(): DataSourceEntity =
    DataSourceEntity(
        id = id,
        type = type.name,
        displayName = displayName,
        status = status.name,
        priority = priority,
        lastSyncAtEpochMillis = lastSyncAt?.toEpochMilli(),
        lastError = lastError,
    )

internal fun MetricRecordEntity.toDomain(): MetricRecord =
    MetricRecord(
        id = id,
        metricType = com.cyberdoc.app.domain.model.MetricType.valueOf(metricType),
        value = value,
        unit = unit,
        startAt = Instant.ofEpochMilli(startAtEpochMillis),
        endAt = Instant.ofEpochMilli(endAtEpochMillis),
        sourceId = sourceId,
        externalId = externalId,
        isManual = isManual,
        createdAt = Instant.ofEpochMilli(createdAtEpochMillis),
    )

internal fun MetricRecord.toEntity(): MetricRecordEntity =
    MetricRecordEntity(
        id = id,
        metricType = metricType.name,
        value = value,
        unit = unit,
        startAtEpochMillis = startAt.toEpochMilli(),
        endAtEpochMillis = endAt.toEpochMilli(),
        sourceId = sourceId,
        externalId = externalId,
        isManual = isManual,
        createdAtEpochMillis = createdAt.toEpochMilli(),
    )

internal fun DailyAggregateEntity.toDomain(): DailyAggregate =
    DailyAggregate(
        id = id,
        date = LocalDate.parse(date),
        metricType = com.cyberdoc.app.domain.model.MetricType.valueOf(metricType),
        value = value,
        unit = unit,
        sourceId = sourceId,
        qualityFlag = QualityFlag.valueOf(qualityFlag),
        computedAt = Instant.ofEpochMilli(computedAtEpochMillis),
    )

internal fun DailyAggregate.toEntity(): DailyAggregateEntity =
    DailyAggregateEntity(
        id = id,
        date = date.toString(),
        metricType = metricType.name,
        value = value,
        unit = unit,
        sourceId = sourceId,
        qualityFlag = qualityFlag.name,
        computedAtEpochMillis = computedAt.toEpochMilli(),
    )

internal fun GoalEntity.toDomain(): Goal =
    Goal(
        id = id,
        metricType = com.cyberdoc.app.domain.model.MetricType.valueOf(metricType),
        targetValue = targetValue,
        periodType = PeriodType.valueOf(periodType),
        startDate = LocalDate.parse(startDate),
        endDate = endDate?.let(LocalDate::parse),
        isActive = isActive,
    )

internal fun Goal.toEntity(): GoalEntity =
    GoalEntity(
        id = id,
        metricType = metricType.name,
        targetValue = targetValue,
        periodType = periodType.name,
        startDate = startDate.toString(),
        endDate = endDate?.toString(),
        isActive = isActive,
    )

internal fun SyncRunEntity.toDomain(): SyncRun =
    SyncRun(
        id = id,
        sourceId = sourceId,
        startedAt = Instant.ofEpochMilli(startedAtEpochMillis),
        endedAt = Instant.ofEpochMilli(endedAtEpochMillis),
        status = SyncStatus.valueOf(status),
        recordsRead = recordsRead,
        message = message,
    )

internal fun SyncRun.toEntity(): SyncRunEntity =
    SyncRunEntity(
        id = id,
        sourceId = sourceId,
        startedAtEpochMillis = startedAt.toEpochMilli(),
        endedAtEpochMillis = endedAt.toEpochMilli(),
        status = status.name,
        recordsRead = recordsRead,
        message = message,
    )
