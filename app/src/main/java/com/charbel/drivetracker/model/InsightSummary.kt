package com.charbel.drivetracker.model

data class InsightSummary(
    val totalDistanceMeters: Double = 0.0,
    val totalDurationSeconds: Long = 0L,
    val driveCount: Int = 0,
)

data class WeeklyDistanceBar(
    val label: String,
    val distanceMeters: Double,
)

