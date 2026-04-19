package com.charbel.drivetracker.util

import java.text.DecimalFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val distanceFormat = DecimalFormat("#0.0")
private val speedFormat = DecimalFormat("#0.0")
private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.getDefault())
private val titleDateFormatter = DateTimeFormatter.ofPattern("dd MMM", Locale.getDefault())

fun formatDistance(distanceMeters: Double): String =
    "${distanceFormat.format(distanceMeters / 1000.0)} km"

fun formatSpeed(speedKmh: Double): String =
    "${speedFormat.format(speedKmh)} km/h"

fun formatDuration(totalSeconds: Long): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return when {
        hours > 0 -> String.format(Locale.getDefault(), "%dh %02dm", hours, minutes)
        minutes > 0 -> String.format(Locale.getDefault(), "%dm %02ds", minutes, seconds)
        else -> String.format(Locale.getDefault(), "%ds", seconds)
    }
}

fun formatTripDate(epochMillis: Long): String =
    Instant.ofEpochMilli(epochMillis)
        .atZone(ZoneId.systemDefault())
        .format(dateTimeFormatter)

fun buildTripTitle(
    startAddress: String?,
    endAddress: String?,
    startedAtMillis: Long,
): String {
    return when {
        !startAddress.isNullOrBlank() && !endAddress.isNullOrBlank() -> "$startAddress to $endAddress"
        !startAddress.isNullOrBlank() -> startAddress
        !endAddress.isNullOrBlank() -> endAddress
        else -> "Drive on " + Instant.ofEpochMilli(startedAtMillis)
            .atZone(ZoneId.systemDefault())
            .format(titleDateFormatter)
    }
}

fun Float.toSpeedKmh(): Double = this * 3.6

