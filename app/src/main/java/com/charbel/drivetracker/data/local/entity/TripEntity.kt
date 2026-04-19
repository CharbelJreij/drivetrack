package com.charbel.drivetracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.charbel.drivetracker.model.SyncStatus

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val ownerUserId: String,
    val title: String,
    val startedAtMillis: Long,
    val endedAtMillis: Long,
    val distanceMeters: Double,
    val durationSeconds: Long,
    val averageSpeedKmh: Double,
    val maxSpeedKmh: Double,
    val startAddress: String?,
    val endAddress: String?,
    val syncStatus: SyncStatus,
    val syncedAtMillis: Long? = null,
)
