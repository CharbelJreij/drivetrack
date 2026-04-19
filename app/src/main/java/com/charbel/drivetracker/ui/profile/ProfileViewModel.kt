package com.charbel.drivetracker.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charbel.drivetracker.data.repository.AuthRepository
import com.charbel.drivetracker.data.repository.TripRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class ProfileUiState(
    val email: String? = null,
    val pendingTripCount: Int = 0,
)

class ProfileViewModel(
    private val authRepository: AuthRepository,
    tripRepository: TripRepository,
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = combine(
        authRepository.session,
        tripRepository.observeUnsyncedTripCount(),
    ) { session, pendingTripCount ->
            ProfileUiState(
                email = session?.email,
                pendingTripCount = pendingTripCount,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ProfileUiState(),
        )

    fun signOut() {
        authRepository.signOut()
    }
}
