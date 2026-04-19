package com.charbel.drivetracker.data.local.mapper

import com.charbel.drivetracker.data.local.entity.TripEntity
import com.charbel.drivetracker.data.local.entity.TripPointEntity
import com.charbel.drivetracker.data.local.entity.TripWithPoints
import com.charbel.drivetracker.model.Trip
import com.charbel.drivetracker.model.TripDraft
import com.charbel.drivetracker.model.TripPoint

fun TripWithPoints.toModel(): Trip = Trip(
    id = trip.id,
    title = trip.title,
    startedAtMillis = trip.startedAtMillis,
    endedAtMillis = trip.endedAtMillis,
    distanceMeters = trip.distanceMeters,
    durationSeconds = trip.durationSeconds,
    averageSpeedKmh = trip.averageSpeedKmh,
    maxSpeedKmh = trip.maxSpeedKmh,
    startAddress = trip.startAddress,
    endAddress = trip.endAddress,
    syncStatus = trip.syncStatus,
    syncedAtMillis = trip.syncedAtMillis,
    points = points.sortedBy { it.sequenceIndex }.map { it.toModel() },
)

fun TripPointEntity.toModel(): TripPoint = TripPoint(
    latitude = latitude,
    longitude = longitude,
    recordedAtMillis = recordedAtMillis,
    speedMetersPerSecond = speedMetersPerSecond,
)

fun TripDraft.toTripEntity(ownerUserId: String): TripEntity = TripEntity(
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
)

fun TripDraft.toPointEntities(): List<TripPointEntity> = points.mapIndexed { index, point ->
    TripPointEntity(
        tripId = 0L,
        latitude = point.latitude,
        longitude = point.longitude,
        recordedAtMillis = point.recordedAtMillis,
        speedMetersPerSecond = point.speedMetersPerSecond,
        sequenceIndex = index,
    )
}
