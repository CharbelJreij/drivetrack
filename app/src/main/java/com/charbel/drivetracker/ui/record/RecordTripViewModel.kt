package com.charbel.drivetracker.ui.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charbel.drivetracker.model.TrackingSession
import com.charbel.drivetracker.tracking.TripTrackerManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class RecordTripUiState(
    val session: TrackingSession = TrackingSession(),
)

class RecordTripViewModel(
    private val tripTrackerManager: TripTrackerManager,
) : ViewModel() {

    private val _savedTripIds = MutableSharedFlow<Long>(extraBufferCapacity = 1)
    val savedTripIds = _savedTripIds

    val uiState: StateFlow<RecordTripUiState> = tripTrackerManager.session
        .map { session -> RecordTripUiState(session = session) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = RecordTripUiState(),
        )

    fun startTracking() {
        viewModelScope.launch {
            tripTrackerManager.startTracking()
        }
    }

    fun stopTracking() {
        viewModelScope.launch {
            val tripId = tripTrackerManager.stopTracking()
            if (tripId != null) {
                _savedTripIds.emit(tripId)
            }
        }
    }

    fun dismissMessage() {
        tripTrackerManager.dismissMessage()
    }
}

