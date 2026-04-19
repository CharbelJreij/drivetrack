package com.charbel.drivetracker.ui.tripdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charbel.drivetracker.data.repository.TripRepository
import com.charbel.drivetracker.model.Trip
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class TripDetailUiState(
    val trip: Trip? = null,
)

class TripDetailViewModel(
    tripId: Long,
    tripRepository: TripRepository,
) : ViewModel() {

    val uiState: StateFlow<TripDetailUiState> = tripRepository.observeTrip(tripId)
        .map { trip -> TripDetailUiState(trip = trip) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = TripDetailUiState(),
        )
}

