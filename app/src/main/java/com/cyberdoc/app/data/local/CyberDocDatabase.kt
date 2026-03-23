package com.cyberdoc.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cyberdoc.app.data.local.dao.DailyAggregateDao
import com.cyberdoc.app.data.local.dao.DataSourceDao
import com.cyberdoc.app.data.local.dao.GoalDao
import com.cyberdoc.app.data.local.dao.MetricRecordDao
import com.cyberdoc.app.data.local.dao.SyncRunDao
import com.cyberdoc.app.data.local.entity.DailyAggregateEntity
import com.cyberdoc.app.data.local.entity.DataSourceEntity
import com.cyberdoc.app.data.local.entity.GoalEntity
import com.cyberdoc.app.data.local.entity.MetricRecordEntity
import com.cyberdoc.app.data.local.entity.SyncRunEntity

@Database(
    entities = [
        DataSourceEntity::class,
        MetricRecordEntity::class,
        DailyAggregateEntity::class,
        GoalEntity::class,
        SyncRunEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class CyberDocDatabase : RoomDatabase() {
    abstract fun dataSourceDao(): DataSourceDao
    abstract fun metricRecordDao(): MetricRecordDao
    abstract fun dailyAggregateDao(): DailyAggregateDao
    abstract fun goalDao(): GoalDao
    abstract fun syncRunDao(): SyncRunDao

    companion object {
        fun create(context: Context): CyberDocDatabase =
            Room.databaseBuilder(
                context,
                CyberDocDatabase::class.java,
                "cyberdoc.db",
            )
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
    }
}
