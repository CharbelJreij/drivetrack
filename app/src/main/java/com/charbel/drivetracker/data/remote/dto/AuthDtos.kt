package com.charbel.drivetracker.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PasswordAuthRequestDto(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
)

data class RefreshTokenRequestDto(
    @SerializedName("refresh_token")
    val refreshToken: String,
)

data class AuthSessionResponseDto(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("expires_in")
    val expiresInSeconds: Long,
    @SerializedName("user")
    val user: AuthUserDto,
)

data class AuthUserDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("email")
    val email: String?,
)
