package com.charbel.drivetracker.data.remote

import com.charbel.drivetracker.data.remote.dto.AuthSessionResponseDto
import com.charbel.drivetracker.data.remote.dto.PasswordAuthRequestDto
import com.charbel.drivetracker.data.remote.dto.RemoteTripPointRecordDto
import com.charbel.drivetracker.data.remote.dto.RemoteTripRecordDto
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

    @GET("rest/v1/trips")
    suspend fun getTrips(
        @Header("Authorization") authHeader: String,
        @Query("select")
        select: String = "id,client_local_id,title,started_at_millis,ended_at_millis,distance_meters,duration_seconds,average_speed_kmh,max_speed_kmh,start_address,end_address",
        @Query("order") order: String = "started_at_millis.desc",
    ): List<RemoteTripRecordDto>

    @GET("rest/v1/trip_points")
    suspend fun getTripPoints(
        @Header("Authorization") authHeader: String,
        @Query("select")
        select: String = "trip_id,sequence_index,latitude,longitude,recorded_at_millis,speed_meters_per_second",
        @Query("trip_id") tripIdFilter: String,
        @Query("order") order: String = "sequence_index.asc",
    ): List<RemoteTripPointRecordDto>

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
