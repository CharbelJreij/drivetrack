package com.charbel.drivetracker.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charbel.drivetracker.data.repository.TripRepository
import com.charbel.drivetracker.model.Trip
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class HistoryUiState(
    val query: String = "",
    val trips: List<Trip> = emptyList(),
)

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModel(
    tripRepository: TripRepository,
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val trips = query.flatMapLatest { tripRepository.observeTrips(it) }

    val uiState: StateFlow<HistoryUiState> = combine(query, trips) { currentQuery, currentTrips ->
        HistoryUiState(
            query = currentQuery,
            trips = currentTrips,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = HistoryUiState(),
    )

    fun updateQuery(newQuery: String) {
        query.update { newQuery }
    }
}
