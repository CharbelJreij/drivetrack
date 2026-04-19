package com.charbel.drivetracker.data.remote

import com.charbel.drivetracker.data.remote.dto.OpenRouteServiceReverseResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenRouteServiceApi {

    @GET("geocode/reverse")
    suspend fun reverseGeocode(
        @Query("point.lat") latitude: Double,
        @Query("point.lon") longitude: Double,
        @Query("size") resultSize: Int = 1,
    ): OpenRouteServiceReverseResponseDto
}
