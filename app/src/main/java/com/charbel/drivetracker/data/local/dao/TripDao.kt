package com.charbel.drivetracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.charbel.drivetracker.data.local.entity.TripEntity
import com.charbel.drivetracker.data.local.entity.TripPointEntity
import com.charbel.drivetracker.data.local.entity.TripWithPoints
import com.charbel.drivetracker.model.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {

    @Transaction
    @Query(
        """
        SELECT * FROM trips
        WHERE ownerUserId = :ownerUserId
          AND (
              (:query = '')
              OR title LIKE '%' || :query || '%'
              OR IFNULL(startAddress, '') LIKE '%' || :query || '%'
              OR IFNULL(endAddress, '') LIKE '%' || :query || '%'
          )
        ORDER BY startedAtMillis DESC
        """,
    )
    fun observeTrips(
        query: String,
        ownerUserId: String,
    ): Flow<List<TripWithPoints>>

    @Transaction
    @Query("SELECT * FROM trips WHERE ownerUserId = :ownerUserId ORDER BY startedAtMillis DESC LIMIT :limit")
    fun observeRecentTrips(
        limit: Int,
        ownerUserId: String,
    ): Flow<List<TripWithPoints>>

    @Transaction
    @Query("SELECT * FROM trips WHERE id = :tripId AND ownerUserId = :ownerUserId")
    fun observeTrip(
        tripId: Long,
        ownerUserId: String,
    ): Flow<TripWithPoints?>

    @Transaction
    @Query(
        """
        SELECT * FROM trips
        WHERE ownerUserId = :ownerUserId
          AND startedAtMillis BETWEEN :startMillis AND :endMillis
        ORDER BY startedAtMillis DESC
        """,
    )
    fun observeTripsBetween(
        startMillis: Long,
        endMillis: Long,
        ownerUserId: String,
    ): Flow<List<TripWithPoints>>

    @Query(
        """
        SELECT COUNT(*)
        FROM trips
        WHERE ownerUserId = :ownerUserId
          AND syncStatus != :syncedStatus
        """,
    )
    fun observeUnsyncedTripCount(
        ownerUserId: String,
        syncedStatus: SyncStatus = SyncStatus.SYNCED,
    ): Flow<Int>

    @Insert
    suspend fun insertTrip(trip: TripEntity): Long

    @Insert
    suspend fun insertTripPoints(points: List<TripPointEntity>)

    @Transaction
    suspend fun insertTripWithPoints(trip: TripEntity, points: List<TripPointEntity>): Long {
        val tripId = insertTrip(trip)
        insertTripPoints(points.map { point -> point.copy(tripId = tripId) })
        return tripId
    }

    @Query(
        """
        SELECT id
        FROM trips
        WHERE ownerUserId = :ownerUserId
          AND startedAtMillis = :startedAtMillis
          AND endedAtMillis = :endedAtMillis
          AND durationSeconds = :durationSeconds
        LIMIT 1
        """,
    )
    suspend fun findTripIdByImportSignature(
        ownerUserId: String,
        startedAtMillis: Long,
        endedAtMillis: Long,
        durationSeconds: Long,
    ): Long?

    @Transaction
    @Query("SELECT * FROM trips WHERE ownerUserId = :ownerUserId AND syncStatus = :status ORDER BY startedAtMillis ASC")
    suspend fun getTripsBySyncStatus(
        status: SyncStatus,
        ownerUserId: String,
    ): List<TripWithPoints>

    @Query("UPDATE trips SET syncStatus = :status, syncedAtMillis = :syncedAtMillis WHERE ownerUserId = :ownerUserId AND id IN (:tripIds)")
    suspend fun updateSyncStatus(
        tripIds: List<Long>,
        status: SyncStatus,
        syncedAtMillis: Long?,
        ownerUserId: String,
    )
}
