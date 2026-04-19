package com.charbel.drivetracker.data.repository

import com.charbel.drivetracker.data.auth.AuthLocalDataSource
import com.charbel.drivetracker.data.auth.AuthSession
import com.charbel.drivetracker.data.remote.SupabaseApiService
import com.charbel.drivetracker.data.remote.SupabaseConfig
import com.charbel.drivetracker.data.remote.dto.AuthSessionResponseDto
import com.charbel.drivetracker.data.remote.dto.PasswordAuthRequestDto
import com.charbel.drivetracker.data.remote.dto.RefreshTokenRequestDto
import kotlinx.coroutines.flow.StateFlow

class SupabaseAuthRepository(
    private val api: SupabaseApiService,
    private val localDataSource: AuthLocalDataSource,
    private val config: SupabaseConfig,
) : AuthRepository {

    override val session: StateFlow<AuthSession?> = localDataSource.session

    override val isConfigured: Boolean
        get() = config.isConfigured

    override suspend fun signIn(email: String, password: String): AuthSession {
        check(config.isConfigured) { "Supabase is not configured." }

        val response = api.signIn(
            request = PasswordAuthRequestDto(
                email = email.trim(),
                password = password,
            ),
        )
        val session = response.toAuthSession()
        localDataSource.saveSession(session)
        return session
    }

    override suspend fun signUp(email: String, password: String): AuthSubmitResult {
        check(config.isConfigured) { "Supabase is not configured." }

        api.signUp(
            request = PasswordAuthRequestDto(
                email = email.trim(),
                password = password,
            ),
        )

        return runCatching {
            signIn(email = email, password = password)
        }.fold(
            onSuccess = { session -> AuthSubmitResult.Success(session) },
            onFailure = { AuthSubmitResult.EmailConfirmationRequired(email = email.trim()) },
        )
    }

    override suspend fun refreshSessionIfNeeded(): AuthSession? {
        if (!config.isConfigured) return null

        val currentSession = localDataSource.currentSession() ?: return null
        if (!currentSession.isExpiringSoon()) return currentSession

        val refreshed = api.refreshSession(
            request = RefreshTokenRequestDto(refreshToken = currentSession.refreshToken),
        ).toAuthSession()
        localDataSource.saveSession(refreshed)
        return refreshed
    }

    override suspend fun getValidAccessToken(): String? =
        refreshSessionIfNeeded()?.accessToken ?: localDataSource.currentSession()?.accessToken

    override fun signOut() {
        localDataSource.clearSession()
    }

    private fun AuthSessionResponseDto.toAuthSession(): AuthSession = AuthSession(
        accessToken = accessToken,
        refreshToken = refreshToken,
        userId = user.id,
        email = user.email,
        expiresAtMillis = System.currentTimeMillis() + (expiresInSeconds * 1_000L),
    )
}
