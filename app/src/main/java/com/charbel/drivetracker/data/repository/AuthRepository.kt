package com.charbel.drivetracker.data.repository

import com.charbel.drivetracker.data.auth.AuthSession
import kotlinx.coroutines.flow.StateFlow

enum class AuthSubmitMode {
    SIGN_IN,
    SIGN_UP,
}

sealed interface AuthSubmitResult {
    data class Success(val session: AuthSession) : AuthSubmitResult
    data class EmailConfirmationRequired(val email: String) : AuthSubmitResult
}

interface AuthRepository {
    val session: StateFlow<AuthSession?>

    val isConfigured: Boolean

    suspend fun signIn(email: String, password: String): AuthSession

    suspend fun signUp(email: String, password: String): AuthSubmitResult

    suspend fun refreshSessionIfNeeded(): AuthSession?

    suspend fun getValidAccessToken(): String?

    fun signOut()
}
