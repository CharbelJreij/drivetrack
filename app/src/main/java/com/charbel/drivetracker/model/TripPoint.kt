package com.charbel.drivetracker.model

data class TripPoint(
    val latitude: Double,
    val longitude: Double,
    val recordedAtMillis: Long,
    val speedMetersPerSecond: Float,
)

