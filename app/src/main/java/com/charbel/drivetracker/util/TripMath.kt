package com.charbel.drivetracker.util

import android.location.Location
import com.charbel.drivetracker.model.TripPoint

fun distanceBetweenMeters(first: TripPoint, second: TripPoint): Double {
    val results = FloatArray(1)
    Location.distanceBetween(
        first.latitude,
        first.longitude,
        second.latitude,
        second.longitude,
        results,
    )
    return results.firstOrNull()?.toDouble() ?: 0.0
}

