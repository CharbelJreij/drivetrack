package com.charbel.drivetracker.data.remote

import com.charbel.drivetracker.data.remote.dto.formattedLabel

class OpenRouteServiceGeocoder(
    private val api: OpenRouteServiceApi,
    private val config: OpenRouteServiceConfig,
) {

    suspend fun reverseGeocode(
        latitude: Double,
        longitude: Double,
    ): String? {
        if (!config.isConfigured) return null

        return try {
            api.reverseGeocode(
                latitude = latitude,
                longitude = longitude,
            ).features.firstOrNull()?.properties?.formattedLabel()
        } catch (_: Exception) {
            null
        }
    }
}
