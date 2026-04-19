package com.charbel.drivetracker.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charbel.drivetracker.data.repository.AuthRepository
import com.charbel.drivetracker.data.repository.TripRepository
import com.charbel.drivetracker.model.InsightSummary
import com.charbel.drivetracker.model.TrackingSession
import com.charbel.drivetracker.model.Trip
import com.charbel.drivetracker.tracking.TripTrackerManager
import com.charbel.drivetracker.util.currentWeekWindow
import com.charbel.drivetracker.util.toInsightSummary
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val trackingSession: TrackingSession = TrackingSession(),
    val weeklySummary: InsightSummary = InsightSummary(),
    val recentTrips: List<Trip> = emptyList(),
    val accountEmail: String? = null,
)

class HomeViewModel(
    tripRepository: TripRepository,
    private val tripTrackerManager: TripTrackerManager,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val weekWindow = currentWeekWindow()

    val uiState: StateFlow<HomeUiState> = combine(
        tripTrackerManager.session,
        tripRepository.observeRecentTrips(),
        tripRepository.observeTripsBetween(weekWindow.startMillis, weekWindow.endMillis),
        authRepository.session,
    ) { session, recentTrips, weeklyTrips, authSession ->
        HomeUiState(
            trackingSession = session,
            weeklySummary = weeklyTrips.toInsightSummary(),
            recentTrips = recentTrips,
            accountEmail = authSession?.email,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = HomeUiState(),
    )

    fun dismissMessage() {
        tripTrackerManager.dismissMessage()
    }

    fun signOut() {
        authRepository.signOut()
    }
}
