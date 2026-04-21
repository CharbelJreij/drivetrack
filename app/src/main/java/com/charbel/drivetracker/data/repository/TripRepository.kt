package com.charbel.drivetracker.data.repository

import com.charbel.drivetracker.model.Trip
import com.charbel.drivetracker.model.TripDraft
import kotlinx.coroutines.flow.Flow

interface TripRepository {

    fun observeRecentTrips(limit: Int = 5): Flow<List<Trip>>

    fun observeTrips(query: String): Flow<List<Trip>>

    fun observeTrip(tripId: Long): Flow<Trip?>

    fun observeTripsBetween(startMillis: Long, endMillis: Long): Flow<List<Trip>>

    fun observeUnsyncedTripCount(): Flow<Int>

    suspend fun saveTrip(draft: TripDraft): Long

    suspend fun getPendingTrips(): List<Trip>

    suspend fun markTripsSynced(tripIds: List<Long>, syncedAtMillis: Long)

    suspend fun markTripsFailed(tripIds: List<Long>)

    suspend fun refreshTripsFromRemote()
}
