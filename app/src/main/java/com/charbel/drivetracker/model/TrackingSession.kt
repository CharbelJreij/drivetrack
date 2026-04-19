package com.charbel.drivetracker.model

data class TrackingSession(
    val isTracking: Boolean = false,
    val startedAtMillis: Long? = null,
    val points: List<TripPoint> = emptyList(),
    val distanceMeters: Double = 0.0,
    val durationSeconds: Long = 0L,
    val averageSpeedKmh: Double = 0.0,
    val maxSpeedKmh: Double = 0.0,
    val startAddress: String? = null,
    val endAddress: String? = null,
    val message: String? = null,
)

