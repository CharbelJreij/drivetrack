package com.charbel.drivetracker.ui.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charbel.drivetracker.data.repository.TripRepository
import com.charbel.drivetracker.model.InsightSummary
import com.charbel.drivetracker.model.Trip
import com.charbel.drivetracker.model.WeeklyDistanceBar
import com.charbel.drivetracker.util.buildWeeklyDistanceBars
import com.charbel.drivetracker.util.currentMonthWindow
import com.charbel.drivetracker.util.currentWeekWindow
import com.charbel.drivetracker.util.recentWeeksWindow
import com.charbel.drivetracker.util.toInsightSummary
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class InsightsUiState(
    val weeklySummary: InsightSummary = InsightSummary(),
    val monthlySummary: InsightSummary = InsightSummary(),
    val weeklyBars: List<WeeklyDistanceBar> = emptyList(),
)

class InsightsViewModel(
    tripRepository: TripRepository,
) : ViewModel() {

    private val recentWindow = recentWeeksWindow(numberOfWeeks = 6)
    private val weekWindow = currentWeekWindow()
    private val monthWindow = currentMonthWindow()
    private val analysisStart = minOf(recentWindow.startMillis, weekWindow.startMillis, monthWindow.startMillis)
    private val analysisEnd = maxOf(recentWindow.endMillis, weekWindow.endMillis, monthWindow.endMillis)

    val uiState: StateFlow<InsightsUiState> = tripRepository.observeTripsBetween(
        startMillis = analysisStart,
        endMillis = analysisEnd,
    ).map { trips ->
        InsightsUiState(
            weeklySummary = trips.filteredByRange(weekWindow.startMillis, weekWindow.endMillis).toInsightSummary(),
            monthlySummary = trips.filteredByRange(monthWindow.startMillis, monthWindow.endMillis).toInsightSummary(),
            weeklyBars = buildWeeklyDistanceBars(trips),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = InsightsUiState(),
    )

    private fun List<Trip>.filteredByRange(startMillis: Long, endMillis: Long): List<Trip> =
        filter { trip -> trip.startedAtMillis in startMillis..endMillis }
}

