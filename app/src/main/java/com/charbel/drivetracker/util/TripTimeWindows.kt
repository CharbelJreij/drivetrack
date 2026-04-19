package com.charbel.drivetracker.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId

data class TimeWindow(
    val startMillis: Long,
    val endMillis: Long,
)

fun currentWeekWindow(): TimeWindow {
    val today = LocalDate.now()
    val weekStart = today.with(DayOfWeek.MONDAY)
    return weekStart.toWindow(endDate = weekStart.plusDays(6))
}

fun currentMonthWindow(): TimeWindow {
    val today = LocalDate.now()
    val monthStart = today.withDayOfMonth(1)
    return monthStart.toWindow(endDate = today.withDayOfMonth(today.lengthOfMonth()))
}

fun recentWeeksWindow(numberOfWeeks: Int): TimeWindow {
    val today = LocalDate.now()
    val start = today.minusWeeks((numberOfWeeks - 1).toLong()).with(DayOfWeek.MONDAY)
    return start.toWindow(endDate = today)
}

private fun LocalDate.toWindow(endDate: LocalDate): TimeWindow {
    val zoneId = ZoneId.systemDefault()
    return TimeWindow(
        startMillis = atStartOfDay(zoneId).toInstant().toEpochMilli(),
        endMillis = endDate.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1L,
    )
}
