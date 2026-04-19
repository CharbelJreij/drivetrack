package com.charbel.drivetracker.data.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.charbel.drivetracker.data.worker.TripSyncWorker

class TripSyncScheduler(
    context: Context,
) {

    private val workManager = WorkManager.getInstance(context)

    fun enqueueSync() {
        val request = OneTimeWorkRequestBuilder<TripSyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build(),
            )
            .build()

        workManager.enqueueUniqueWork(
            SYNC_WORK_NAME,
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            request,
        )
    }

    companion object {
        const val SYNC_WORK_NAME = "trip_sync_work"
    }
}
