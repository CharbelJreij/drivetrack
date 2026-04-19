package com.charbel.drivetracker.data.auth

data class AuthSession(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
    val email: String?,
    val expiresAtMillis: Long,
) {
    val bearerToken: String
        get() = "Bearer $accessToken"

    fun isExpiringSoon(nowMillis: Long = System.currentTimeMillis()): Boolean =
        nowMillis >= expiresAtMillis - REFRESH_BUFFER_MILLIS

    companion object {
        private const val REFRESH_BUFFER_MILLIS = 60_000L
    }
}
