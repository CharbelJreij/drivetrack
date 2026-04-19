package com.charbel.drivetracker.util

import com.charbel.drivetracker.model.InsightSummary
import com.charbel.drivetracker.model.Trip
import com.charbel.drivetracker.model.WeeklyDistanceBar
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

fun List<Trip>.toInsightSummary(): InsightSummary = InsightSummary(
    totalDistanceMeters = sumOf { it.distanceMeters },
    totalDurationSeconds = sumOf { it.durationSeconds },
    driveCount = size,
)

fun buildWeeklyDistanceBars(
    trips: List<Trip>,
    numberOfWeeks: Int = 6,
): List<WeeklyDistanceBar> {
    val today = LocalDate.now()
    return (numberOfWeeks - 1 downTo 0).map { weeksAgo ->
        val weekStart = today.minusWeeks(weeksAgo.toLong()).with(java.time.DayOfWeek.MONDAY)
        val weekEnd = weekStart.plusDays(6)
        val distance = trips
            .filter { trip ->
                val localDate = Instant.ofEpochMilli(trip.startedAtMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                !localDate.isBefore(weekStart) && !localDate.isAfter(weekEnd)
            }
            .sumOf { it.distanceMeters }

        WeeklyDistanceBar(
            label = weekStart.dayOfMonth.toString() + " " +
                weekStart.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
            distanceMeters = distance,
        )
    }
}

