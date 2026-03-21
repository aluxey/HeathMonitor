package com.cyberdoc.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: String,
    val timezone: String,
    @ColumnInfo(name = "weight_unit") val weightUnit: String,
    @ColumnInfo(name = "energy_unit") val energyUnit: String,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
)
