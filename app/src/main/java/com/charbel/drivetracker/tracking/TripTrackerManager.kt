package com.charbel.drivetracker.tracking

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import com.charbel.drivetracker.R
import com.charbel.drivetracker.data.remote.OpenRouteServiceGeocoder
import com.charbel.drivetracker.data.repository.TripRepository
import com.charbel.drivetracker.data.sync.TripSyncScheduler
import com.charbel.drivetracker.model.TrackingSession
import com.charbel.drivetracker.model.TripDraft
import com.charbel.drivetracker.model.TripPoint
import com.charbel.drivetracker.util.buildTripTitle
import com.charbel.drivetracker.util.distanceBetweenMeters
import com.charbel.drivetracker.util.toSpeedKmh
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.math.max

class TripTrackerManager(
    context: Context,
    private val tripRepository: TripRepository,
    private val syncScheduler: TripSyncScheduler,
    private val openRouteServiceGeocoder: OpenRouteServiceGeocoder,
) {

    private val appContext = context.applicationContext
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(appContext)
    private val trackerScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val sessionState = MutableStateFlow(TrackingSession())

    private var timerJob: Job? = null

    val session: StateFlow<TrackingSession> = sessionState.asStateFlow()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.locations.forEach { location ->
                appendPoint(location)
            }
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun startTracking(): Boolean {
        if (sessionState.value.isTracking) return true

        sessionState.value = TrackingSession(
            isTracking = true,
            startedAtMillis = System.currentTimeMillis(),
        )

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            LOCATION_UPDATE_INTERVAL_MILLIS,
        )
            .setMinUpdateDistanceMeters(MIN_UPDATE_DISTANCE_METERS)
            .build()

        return try {
            fusedLocationClient.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper(),
            ).await()

            startTimer()
            true
        } catch (_: SecurityException) {
            sessionState.value = TrackingSession(
                message = appContext.getString(R.string.location_error),
            )
            false
        } catch (_: Exception) {
            sessionState.value = TrackingSession(
                message = appContext.getString(R.string.location_error),
            )
            false
        }
    }

    suspend fun stopTracking(): Long? {
        val currentSession = sessionState.value
        if (!currentSession.isTracking || currentSession.startedAtMillis == null) {
            return null
        }

        stopLocationUpdates()
        timerJob?.cancel()

        if (currentSession.points.isEmpty()) {
            sessionState.value = TrackingSession(
                message = appContext.getString(R.string.location_error),
            )
            return null
        }

        val startAddress = resolveAddress(currentSession.points.first())
        val endAddress = resolveAddress(currentSession.points.last())

        val draft = TripDraft(
            title = buildTripTitle(
                startAddress = startAddress,
                endAddress = endAddress,
                startedAtMillis = currentSession.startedAtMillis,
            ),
            startedAtMillis = currentSession.startedAtMillis,
            endedAtMillis = System.currentTimeMillis(),
            distanceMeters = currentSession.distanceMeters,
            durationSeconds = currentSession.durationSeconds,
            averageSpeedKmh = currentSession.averageSpeedKmh,
            maxSpeedKmh = currentSession.maxSpeedKmh,
            startAddress = startAddress,
            endAddress = endAddress,
            points = currentSession.points,
        )

        return try {
            val tripId = withContext(Dispatchers.IO) {
                tripRepository.saveTrip(draft)
            }

            syncScheduler.enqueueSync()
            sessionState.value = TrackingSession(
                message = appContext.getString(R.string.save_trip_success),
            )
            tripId
        } catch (_: Exception) {
            sessionState.value = TrackingSession(
                message = appContext.getString(R.string.location_error),
            )
            null
        }
    }

    fun dismissMessage() {
        sessionState.update { it.copy(message = null) }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = trackerScope.launch {
            while (true) {
                delay(1_000L)
                sessionState.update { session ->
                    val startedAt = session.startedAtMillis ?: return@update session
                    session.copy(
                        durationSeconds = ((System.currentTimeMillis() - startedAt) / 1_000L).coerceAtLeast(0L),
                        averageSpeedKmh = calculateAverageSpeedKmh(
                            distanceMeters = session.distanceMeters,
                            durationSeconds = ((System.currentTimeMillis() - startedAt) / 1_000L).coerceAtLeast(0L),
                        ),
                    )
                }
            }
        }
    }

    private fun appendPoint(location: Location) {
        val point = TripPoint(
            latitude = location.latitude,
            longitude = location.longitude,
            recordedAtMillis = location.time,
            speedMetersPerSecond = location.speed.coerceAtLeast(0f),
        )

        sessionState.update { current ->
            if (!current.isTracking) return@update current

            val previousPoint = current.points.lastOrNull()
            val updatedPoints = current.points + point
            val additionalDistance = if (previousPoint == null) {
                0.0
            } else {
                distanceBetweenMeters(previousPoint, point)
            }

            val newDistance = current.distanceMeters + additionalDistance
            val durationSeconds = current.startedAtMillis?.let { startedAt ->
                ((System.currentTimeMillis() - startedAt) / 1_000L).coerceAtLeast(0L)
            } ?: current.durationSeconds

            current.copy(
                points = updatedPoints,
                distanceMeters = newDistance,
                durationSeconds = durationSeconds,
                averageSpeedKmh = calculateAverageSpeedKmh(
                    distanceMeters = newDistance,
                    durationSeconds = durationSeconds,
                ),
                maxSpeedKmh = max(
                    current.maxSpeedKmh,
                    point.speedMetersPerSecond.toSpeedKmh(),
                ),
            )
        }
    }

    private suspend fun stopLocationUpdates() {
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback).await()
        } catch (_: Exception) {
        }
    }

    private suspend fun resolveAddress(point: TripPoint): String? = withContext(Dispatchers.IO) {
        openRouteServiceGeocoder.reverseGeocode(
            latitude = point.latitude,
            longitude = point.longitude,
        )?.let { return@withContext it }

        if (Geocoder.isPresent()) {
            try {
                val geocoder = Geocoder(appContext, Locale.getDefault())
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1)
                val address = addresses?.firstOrNull()
                val localAddress = address?.let {
                    listOfNotNull(
                        it.thoroughfare,
                        it.subLocality,
                        it.locality,
                    ).joinToString(separator = ", ").ifBlank {
                        it.adminArea ?: it.countryName
                    }
                }
                if (!localAddress.isNullOrBlank()) {
                    return@withContext localAddress
                }
            } catch (_: Exception) {
            }
        }

        String.format(
            Locale.US,
            "%.5f, %.5f",
            point.latitude,
            point.longitude,
        )
    }

    private fun calculateAverageSpeedKmh(
        distanceMeters: Double,
        durationSeconds: Long,
    ): Double {
        if (durationSeconds == 0L) return 0.0
        return (distanceMeters / 1_000.0) / (durationSeconds / 3_600.0)
    }

    companion object {
        private const val LOCATION_UPDATE_INTERVAL_MILLIS = 4_000L
        private const val MIN_UPDATE_DISTANCE_METERS = 5f
    }
}
