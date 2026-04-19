package com.charbel.drivetracker.data.remote

import com.charbel.drivetracker.data.remote.dto.AuthSessionResponseDto
import com.charbel.drivetracker.data.remote.dto.PasswordAuthRequestDto
import com.charbel.drivetracker.data.remote.dto.RefreshTokenRequestDto
import com.charbel.drivetracker.data.remote.dto.RemoteTripInsertDto
import com.charbel.drivetracker.data.remote.dto.RemoteTripPointInsertDto
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface SupabaseApiService {

    @POST("auth/v1/signup")
    suspend fun signUp(
        @Body request: PasswordAuthRequestDto,
    ): JsonObject

    @POST("auth/v1/token?grant_type=password")
    suspend fun signIn(
        @Body request: PasswordAuthRequestDto,
    ): AuthSessionResponseDto

    @POST("auth/v1/token?grant_type=refresh_token")
    suspend fun refreshSession(
        @Body request: RefreshTokenRequestDto,
    ): AuthSessionResponseDto

    @POST("rest/v1/trips")
    suspend fun insertTrip(
        @Header("Authorization") authHeader: String,
        @Header("Prefer") prefer: String = "return=representation",
        @Body trip: RemoteTripInsertDto,
    ): List<RemoteTripInsertDto>

    @GET("rest/v1/trips")
    suspend fun findTripByLocalId(
        @Header("Authorization") authHeader: String,
        @Query("select") select: String = "id,client_local_id",
        @Query("client_local_id") clientLocalIdFilter: String,
        @Query("limit") limit: Int = 1,
    ): List<RemoteTripInsertDto>

    @DELETE("rest/v1/trip_points")
    suspend fun deleteTripPoints(
        @Header("Authorization") authHeader: String,
        @Query("trip_id") tripIdFilter: String,
    ): Response<Unit>

    @POST("rest/v1/trip_points")
    suspend fun insertTripPoints(
        @Header("Authorization") authHeader: String,
        @Header("Prefer") prefer: String = "return=minimal",
        @Body points: List<RemoteTripPointInsertDto>,
    ): Response<Unit>
}
