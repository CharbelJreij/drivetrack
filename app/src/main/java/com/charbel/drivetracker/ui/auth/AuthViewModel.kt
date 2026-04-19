package com.charbel.drivetracker.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charbel.drivetracker.data.repository.AuthRepository
import com.charbel.drivetracker.data.repository.AuthSubmitMode
import com.charbel.drivetracker.data.repository.AuthSubmitResult
import com.charbel.drivetracker.data.sync.TripSyncScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val mode: AuthSubmitMode = AuthSubmitMode.SIGN_IN,
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val message: String? = null,
    val isConfigured: Boolean = true,
)

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val tripSyncScheduler: TripSyncScheduler,
) : ViewModel() {

    private val state = MutableStateFlow(
        AuthUiState(isConfigured = authRepository.isConfigured),
    )

    val uiState: StateFlow<AuthUiState> = state.asStateFlow()

    fun updateEmail(value: String) {
        state.update { current -> current.copy(email = value) }
    }

    fun updatePassword(value: String) {
        state.update { current -> current.copy(password = value) }
    }

    fun setMode(mode: AuthSubmitMode) {
        state.update { current ->
            current.copy(
                mode = mode,
                message = null,
            )
        }
    }

    fun dismissMessage() {
        state.update { current -> current.copy(message = null) }
    }

    fun submit() {
        val currentState = state.value
        if (!currentState.isConfigured) {
            state.update { current ->
                current.copy(message = "Add SUPABASE_URL and SUPABASE_ANON_KEY in local.properties first.")
            }
            return
        }
        if (currentState.email.isBlank() || currentState.password.isBlank()) {
            state.update { current ->
                current.copy(message = "Email and password are required.")
            }
            return
        }

        viewModelScope.launch {
            state.update { current -> current.copy(isLoading = true, message = null) }

            runCatching {
                when (state.value.mode) {
                    AuthSubmitMode.SIGN_IN -> authRepository.signIn(
                        email = state.value.email,
                        password = state.value.password,
                    ).let { AuthSubmitResult.Success(it) }

                    AuthSubmitMode.SIGN_UP -> authRepository.signUp(
                        email = state.value.email,
                        password = state.value.password,
                    )
                }
            }.onSuccess { result ->
                when (result) {
                    is AuthSubmitResult.Success -> {
                        tripSyncScheduler.enqueueSync()
                        state.update { current ->
                            current.copy(
                                isLoading = false,
                                password = "",
                                message = null,
                            )
                        }
                    }

                    is AuthSubmitResult.EmailConfirmationRequired -> {
                        state.update { current ->
                            current.copy(
                                isLoading = false,
                                password = "",
                                message = "Account created for ${result.email}. Confirm the email from your inbox, or disable email confirmation in your auth settings for a smoother demo flow.",
                            )
                        }
                    }
                }
            }.onFailure { error ->
                state.update { current ->
                    current.copy(
                        isLoading = false,
                        message = error.message ?: "Authentication failed.",
                    )
                }
            }
        }
    }
}
