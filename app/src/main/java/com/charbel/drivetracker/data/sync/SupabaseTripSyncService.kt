package com.charbel.drivetracker.data.sync

import com.charbel.drivetracker.data.remote.SupabaseApiService
import com.charbel.drivetracker.data.remote.SupabaseConfig
import com.charbel.drivetracker.data.remote.dto.RemoteTripInsertDto
import com.charbel.drivetracker.data.remote.dto.RemoteTripPointInsertDto
import com.charbel.drivetracker.data.repository.AuthRepository
import com.charbel.drivetracker.model.Trip
import retrofit2.HttpException
import java.io.IOException

sealed interface TripSyncOutcome {
    data class Success(val syncedTripIds: List<Long>) : TripSyncOutcome
    data object NoSession : TripSyncOutcome
    data object MissingConfiguration : TripSyncOutcome
    data object Retry : TripSyncOutcome
    data class PermanentFailure(val failedTripIds: List<Long>) : TripSyncOutcome
}

class SupabaseTripSyncService(
    private val api: SupabaseApiService,
    private val authRepository: AuthRepository,
    private val config: SupabaseConfig,
) {

    suspend fun syncTrips(trips: List<Trip>): TripSyncOutcome {
        if (trips.isEmpty()) return TripSyncOutcome.Success(emptyList())
        if (!config.isConfigured) return TripSyncOutcome.MissingConfiguration

        val accessToken = try {
            authRepository.getValidAccessToken()
        } catch (_: Exception) {
            null
        } ?: return TripSyncOutcome.NoSession

        val authHeader = "Bearer $accessToken"

        return try {
            trips.forEach { trip -> syncTrip(authHeader = authHeader, trip = trip) }
            TripSyncOutcome.Success(syncedTripIds = trips.map { trip -> trip.id })
        } catch (exception: HttpException) {
            when (exception.code()) {
                401, 403 -> {
                    authRepository.signOut()
                    TripSyncOutcome.NoSession
                }

                else -> TripSyncOutcome.PermanentFailure(failedTripIds = trips.map { trip -> trip.id })
            }
        } catch (_: IOException) {
            TripSyncOutcome.Retry
        } catch (_: Exception) {
            TripSyncOutcome.Retry
        }
    }

    private suspend fun syncTrip(
        authHeader: String,
        trip: Trip,
    ) {
        val remoteTripId = insertOrResolveRemoteTripId(
            authHeader = authHeader,
            trip = trip,
        )

        api.deleteTripPoints(
            authHeader = authHeader,
            tripIdFilter = "eq.$remoteTripId",
        ).also { response ->
            if (!response.isSuccessful) error("Failed to clear previously synced route points.")
        }

        if (trip.points.isEmpty()) return

        api.insertTripPoints(
            authHeader = authHeader,
            points = trip.points.mapIndexed { index, point ->
                RemoteTripPointInsertDto(
                    tripId = remoteTripId,
                    sequenceIndex = index,
                    latitude = point.latitude,
                    longitude = point.longitude,
                    recordedAtMillis = point.recordedAtMillis,
                    speedMetersPerSecond = point.speedMetersPerSecond,
                )
            },
        ).also { response ->
            if (!response.isSuccessful) error("Failed to upload route points.")
        }
    }

    private suspend fun insertOrResolveRemoteTripId(
        authHeader: String,
        trip: Trip,
    ): String {
        return try {
            api.insertTrip(
                authHeader = authHeader,
                trip = trip.toRemoteInsertDto(),
            ).firstOrNull()?.id ?: error("Supabase did not return an inserted trip id.")
        } catch (exception: HttpException) {
            if (exception.code() != 409) throw exception

            api.findTripByLocalId(
                authHeader = authHeader,
                clientLocalIdFilter = "eq.${trip.id}",
            ).firstOrNull()?.id ?: throw exception
        }
    }

    private fun Trip.toRemoteInsertDto(): RemoteTripInsertDto = RemoteTripInsertDto(
        clientLocalId = id,
        title = title,
        startedAtMillis = startedAtMillis,
        endedAtMillis = endedAtMillis,
        distanceMeters = distanceMeters,
        durationSeconds = durationSeconds,
        averageSpeedKmh = averageSpeedKmh,
        maxSpeedKmh = maxSpeedKmh,
        startAddress = startAddress,
        endAddress = endAddress,
    )
}
