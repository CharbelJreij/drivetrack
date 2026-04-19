package com.charbel.drivetracker.data.remote

import com.charbel.drivetracker.BuildConfig

data class OpenRouteServiceConfig(
    val apiKey: String,
) {
    val isConfigured: Boolean
        get() = apiKey.isNotBlank()

    companion object {
        const val BASE_URL: String = "https://api.openrouteservice.org/"

        fun fromBuildConfig(): OpenRouteServiceConfig = OpenRouteServiceConfig(
            apiKey = BuildConfig.ORS_API_KEY,
        )
    }
}
