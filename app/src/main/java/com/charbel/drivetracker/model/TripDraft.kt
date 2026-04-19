package com.charbel.drivetracker.model

data class TripDraft(
    val title: String,
    val startedAtMillis: Long,
    val endedAtMillis: Long,
    val distanceMeters: Double,
    val durationSeconds: Long,
    val averageSpeedKmh: Double,
    val maxSpeedKmh: Double,
    val startAddress: String?,
    val endAddress: String?,
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val points: List<TripPoint>,
)

