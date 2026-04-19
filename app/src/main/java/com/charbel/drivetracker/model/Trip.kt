package com.charbel.drivetracker.model

data class Trip(
    val id: Long = 0L,
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
    val points: List<TripPoint> = emptyList(),
)

