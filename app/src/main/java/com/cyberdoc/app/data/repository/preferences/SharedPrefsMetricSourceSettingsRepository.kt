package com.cyberdoc.app.data.repository.preferences

import android.content.Context
import com.cyberdoc.app.core.resolveSourceDisplayName
import com.cyberdoc.app.data.local.dao.DataSourceDao
import com.cyberdoc.app.data.local.dao.MetricRecordDao
import com.cyberdoc.app.domain.model.MetricSourceOption
import com.cyberdoc.app.domain.model.MetricSourceSetting
import com.cyberdoc.app.domain.model.MetricType
import com.cyberdoc.app.domain.repository.MetricSourceSettingsRepository
import java.util.Locale

class SharedPrefsMetricSourceSettingsRepository(
    context: Context,
    private val metricDao: MetricRecordDao,
    private val sourceDao: DataSourceDao,
) : MetricSourceSettingsRepository {
    private val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    override suspend fun allSettings(): List<MetricSourceSetting> =
        MetricType.entries.map { setting(it) }

    override suspend fun setting(metricType: MetricType): MetricSourceSetting {
        val selectedSourceId = prefs.getString(preferenceKey(metricType), null)
        val sourceIds = linkedSetOf<String>().apply {
            addAll(metricDao.sourceIdsByMetric(metricType.name))
            selectedSourceId?.let(::add)
        }

        val knownSources = if (sourceIds.isEmpty()) {
            emptyMap()
        } else {
            sourceDao.byIds(sourceIds.toList()).associateBy { it.id }
        }

        val options = sourceIds
            .map { sourceId ->
                MetricSourceOption(
                    sourceId = sourceId,
                    displayName = knownSources[sourceId]?.displayName
                        ?: resolveSourceDisplayName(sourceId = sourceId, sourceName = null),
                )
            }
            .sortedBy { it.displayName.lowercase(Locale.US) }

        val latestSourceId = metricDao.latest(metricType.name)?.sourceId
        return MetricSourceSetting(
            metricType = metricType,
            selectedSourceId = selectedSourceId,
            effectiveSourceId = selectedSourceId ?: latestSourceId,
            options = options,
        )
    }

    override suspend fun setPreferredSource(metricType: MetricType, sourceId: String?) {
        val key = preferenceKey(metricType)
        prefs.edit().apply {
            if (sourceId.isNullOrBlank()) {
                remove(key)
            } else {
                putString(key, sourceId)
            }
        }.apply()
    }

    private fun preferenceKey(metricType: MetricType): String =
        "metric_source_${metricType.name.lowercase(Locale.US)}"

    companion object {
        private const val PREF_NAME = "metric_source_preferences"
    }
}
