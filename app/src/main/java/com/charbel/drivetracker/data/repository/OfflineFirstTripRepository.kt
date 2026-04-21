package com.charbel.drivetracker.data.repository

import com.charbel.drivetracker.data.local.dao.TripDao
import com.charbel.drivetracker.data.local.entity.TripEntity
import com.charbel.drivetracker.data.local.entity.TripPointEntity
import com.charbel.drivetracker.data.local.entity.TripWithPoints
import com.charbel.drivetracker.data.local.mapper.toModel
import com.charbel.drivetracker.data.local.mapper.toPointEntities
import com.charbel.drivetracker.data.local.mapper.toTripEntity
import com.charbel.drivetracker.model.SyncStatus
import com.charbel.drivetracker.model.Trip
import com.charbel.drivetracker.model.TripDraft
import com.charbel.drivetracker.data.sync.SupabaseTripSyncService
import com.charbel.drivetracker.data.sync.RemoteSyncedTrip
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class OfflineFirstTripRepository(
    private val tripDao: TripDao,
    private val authRepository: AuthRepository,
    private val tripSyncService: SupabaseTripSyncService,
) : TripRepository {

    private fun currentUserIdOrNull(): String? = authRepository.session.value?.userId

    private fun requireCurrentUserId(): String =
        currentUserIdOrNull() ?: error("A signed-in account is required to access trips.")

    private fun userScopedTrips(
        block: (ownerUserId: String) -> Flow<List<TripWithPoints>>,
    ): Flow<List<Trip>> = authRepository.session
        .map { session -> session?.userId }
        .distinctUntilChanged()
        .flatMapLatest { ownerUserId ->
            if (ownerUserId.isNullOrBlank()) {
                flowOf(emptyList())
            } else {
                block(ownerUserId).map { trips -> trips.map { it.toModel() } }
            }
        }

    override fun observeRecentTrips(limit: Int): Flow<List<Trip>> =
        userScopedTrips { ownerUserId ->
            tripDao.observeRecentTrips(
                limit = limit,
                ownerUserId = ownerUserId,
            )
        }

    override fun observeTrips(query: String): Flow<List<Trip>> =
        userScopedTrips { ownerUserId ->
            tripDao.observeTrips(
                query = query.trim(),
                ownerUserId = ownerUserId,
            )
        }

    override fun observeTrip(tripId: Long): Flow<Trip?> =
        authRepository.session
            .map { session -> session?.userId }
            .distinctUntilChanged()
            .flatMapLatest { ownerUserId ->
                if (ownerUserId.isNullOrBlank()) {
                    flowOf(null)
                } else {
                    tripDao.observeTrip(
                        tripId = tripId,
                        ownerUserId = ownerUserId,
                    ).map { trip -> trip?.toModel() }
                }
            }

    override fun observeTripsBetween(startMillis: Long, endMillis: Long): Flow<List<Trip>> =
        userScopedTrips { ownerUserId ->
            tripDao.observeTripsBetween(
                startMillis = startMillis,
                endMillis = endMillis,
                ownerUserId = ownerUserId,
            )
        }

    override fun observeUnsyncedTripCount(): Flow<Int> =
        authRepository.session
            .map { session -> session?.userId }
            .distinctUntilChanged()
            .flatMapLatest { ownerUserId ->
                if (ownerUserId.isNullOrBlank()) {
                    flowOf(0)
                } else {
                    tripDao.observeUnsyncedTripCount(ownerUserId = ownerUserId)
                }
            }

    override suspend fun saveTrip(draft: TripDraft): Long =
        tripDao.insertTripWithPoints(
            trip = draft.toTripEntity(ownerUserId = requireCurrentUserId()),
            points = draft.toPointEntities(),
        )

    override suspend fun getPendingTrips(): List<Trip> =
        currentUserIdOrNull()?.let { ownerUserId ->
            tripDao.getTripsBySyncStatus(
                status = SyncStatus.PENDING,
                ownerUserId = ownerUserId,
            ).map { trip -> trip.toModel() }
        } ?: emptyList()

    override suspend fun markTripsSynced(tripIds: List<Long>, syncedAtMillis: Long) {
        val ownerUserId = currentUserIdOrNull() ?: return
        if (tripIds.isNotEmpty()) {
            tripDao.updateSyncStatus(
                tripIds = tripIds,
                status = SyncStatus.SYNCED,
                syncedAtMillis = syncedAtMillis,
                ownerUserId = ownerUserId,
            )
        }
    }

    override suspend fun markTripsFailed(tripIds: List<Long>) {
        val ownerUserId = currentUserIdOrNull() ?: return
        if (tripIds.isNotEmpty()) {
            tripDao.updateSyncStatus(
                tripIds = tripIds,
                status = SyncStatus.FAILED,
                syncedAtMillis = null,
                ownerUserId = ownerUserId,
            )
        }
    }

    override suspend fun refreshTripsFromRemote() {
        val ownerUserId = currentUserIdOrNull() ?: return
        val remoteTrips = tripSyncService.fetchRemoteTrips()
        if (remoteTrips.isEmpty()) return

        val syncedAtMillis = System.currentTimeMillis()
        remoteTrips.forEach { trip ->
            val existingTripId = tripDao.findTripIdByImportSignature(
                ownerUserId = ownerUserId,
                startedAtMillis = trip.startedAtMillis,
                endedAtMillis = trip.endedAtMillis,
                durationSeconds = trip.durationSeconds,
            )
            if (existingTripId == null) {
                tripDao.insertTripWithPoints(
                    trip = trip.toTripEntity(
                        ownerUserId = ownerUserId,
                        syncedAtMillis = syncedAtMillis,
                    ),
                    points = trip.toPointEntities(),
                )
            }
        }
    }

    private fun RemoteSyncedTrip.toTripEntity(
        ownerUserId: String,
        syncedAtMillis: Long,
    ): TripEntity = TripEntity(
        ownerUserId = ownerUserId,
        title = title,
        startedAtMillis = startedAtMillis,
        endedAtMillis = endedAtMillis,
        distanceMeters = distanceMeters,
        durationSeconds = durationSeconds,
        averageSpeedKmh = averageSpeedKmh,
        maxSpeedKmh = maxSpeedKmh,
        startAddress = startAddress,
        endAddress = endAddress,
        syncStatus = syncStatus,
        syncedAtMillis = syncedAtMillis,
    )

    private fun RemoteSyncedTrip.toPointEntities(): List<TripPointEntity> = points.mapIndexed { index, point ->
        TripPointEntity(
            tripId = 0L,
            latitude = point.latitude,
            longitude = point.longitude,
            recordedAtMillis = point.recordedAtMillis,
            speedMetersPerSecond = point.speedMetersPerSecond,
            sequenceIndex = index,
        )
    }
}
