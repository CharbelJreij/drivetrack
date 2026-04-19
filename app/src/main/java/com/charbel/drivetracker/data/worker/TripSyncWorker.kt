package com.charbel.drivetracker.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.charbel.drivetracker.DriveTrackerApplication
import com.charbel.drivetracker.data.sync.TripSyncOutcome

class TripSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val container = (applicationContext as DriveTrackerApplication).container

        return try {
            val pendingTrips = container.tripRepository.getPendingTrips()
            if (pendingTrips.isEmpty()) {
                return Result.success()
            }

            when (val outcome = container.tripSyncService.syncTrips(pendingTrips)) {
                is TripSyncOutcome.Success -> {
                    container.tripRepository.markTripsSynced(
                        tripIds = outcome.syncedTripIds,
                        syncedAtMillis = System.currentTimeMillis(),
                    )
                    Result.success()
                }

                TripSyncOutcome.NoSession,
                TripSyncOutcome.MissingConfiguration -> Result.success()

                TripSyncOutcome.Retry -> Result.retry()

                is TripSyncOutcome.PermanentFailure -> {
                    container.tripRepository.markTripsFailed(outcome.failedTripIds)
                    Result.failure()
                }
            }
        } catch (_: Exception) {
            Result.retry()
        }
    }
}
