package com.cyberdoc.app.data.local

import androidx.room.TypeConverter
import com.cyberdoc.app.domain.model.MetricType
import com.cyberdoc.app.domain.model.PeriodType
import com.cyberdoc.app.domain.model.QualityFlag
import com.cyberdoc.app.domain.model.SourceStatus
import com.cyberdoc.app.domain.model.SourceType
import com.cyberdoc.app.domain.model.SyncStatus

class Converters {
    @TypeConverter
    fun metricTypeFromString(value: String): MetricType = MetricType.valueOf(value)

    @TypeConverter
    fun metricTypeToString(value: MetricType): String = value.name

    @TypeConverter
    fun periodTypeFromString(value: String): PeriodType = PeriodType.valueOf(value)

    @TypeConverter
    fun periodTypeToString(value: PeriodType): String = value.name

    @TypeConverter
    fun qualityFlagFromString(value: String): QualityFlag = QualityFlag.valueOf(value)

    @TypeConverter
    fun qualityFlagToString(value: QualityFlag): String = value.name

    @TypeConverter
    fun sourceTypeFromString(value: String): SourceType = SourceType.valueOf(value)

    @TypeConverter
    fun sourceTypeToString(value: SourceType): String = value.name

    @TypeConverter
    fun sourceStatusFromString(value: String): SourceStatus = SourceStatus.valueOf(value)

    @TypeConverter
    fun sourceStatusToString(value: SourceStatus): String = value.name

    @TypeConverter
    fun syncStatusFromString(value: String): SyncStatus = SyncStatus.valueOf(value)

    @TypeConverter
    fun syncStatusToString(value: SyncStatus): String = value.name
}
