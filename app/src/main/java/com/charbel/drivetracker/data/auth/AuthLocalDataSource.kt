package com.charbel.drivetracker.data.auth

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthLocalDataSource(
    context: Context,
) {

    private val preferences: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    private val sessionState = MutableStateFlow(readSession())

    val session: StateFlow<AuthSession?> = sessionState.asStateFlow()

    fun currentSession(): AuthSession? = sessionState.value

    fun saveSession(session: AuthSession) {
        preferences.edit()
            .putString(KEY_ACCESS_TOKEN, session.accessToken)
            .putString(KEY_REFRESH_TOKEN, session.refreshToken)
            .putString(KEY_USER_ID, session.userId)
            .putString(KEY_EMAIL, session.email)
            .putLong(KEY_EXPIRES_AT, session.expiresAtMillis)
            .apply()
        sessionState.value = session
    }

    fun clearSession() {
        preferences.edit().clear().apply()
        sessionState.value = null
    }

    private fun readSession(): AuthSession? {
        val accessToken = preferences.getString(KEY_ACCESS_TOKEN, null) ?: return null
        val refreshToken = preferences.getString(KEY_REFRESH_TOKEN, null) ?: return null
        val userId = preferences.getString(KEY_USER_ID, null) ?: return null
        val expiresAtMillis = preferences.getLong(KEY_EXPIRES_AT, 0L)

        return AuthSession(
            accessToken = accessToken,
            refreshToken = refreshToken,
            userId = userId,
            email = preferences.getString(KEY_EMAIL, null),
            expiresAtMillis = expiresAtMillis,
        )
    }

    companion object {
        private const val PREFERENCES_NAME = "drive_tracker_auth"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_EMAIL = "email"
        private const val KEY_EXPIRES_AT = "expires_at"
    }
}
