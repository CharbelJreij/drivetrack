package com.charbel.drivetracker.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.viewModelFactory
import com.charbel.drivetracker.DriveTrackerApplication
import com.charbel.drivetracker.ui.auth.AuthViewModel
import com.charbel.drivetracker.ui.dashboard.HomeViewModel
import com.charbel.drivetracker.ui.history.HistoryViewModel
import com.charbel.drivetracker.ui.insights.InsightsViewModel
import com.charbel.drivetracker.ui.profile.ProfileViewModel
import com.charbel.drivetracker.ui.record.RecordTripViewModel
import com.charbel.drivetracker.ui.tripdetail.TripDetailViewModel
import com.charbel.drivetracker.view.tripdetail.TripDetailFragment

object AppViewModelProvider {
    val Factory: ViewModelProvider.Factory = viewModelFactory {
        initializer {
            HomeViewModel(
                tripRepository = driveTrackerApplication().container.tripRepository,
                tripTrackerManager = driveTrackerApplication().container.tripTrackerManager,
                authRepository = driveTrackerApplication().container.authRepository,
            )
        }

        initializer {
            RecordTripViewModel(
                tripTrackerManager = driveTrackerApplication().container.tripTrackerManager,
            )
        }

        initializer {
            HistoryViewModel(
                tripRepository = driveTrackerApplication().container.tripRepository,
            )
        }

        initializer {
            val tripId = createSavedStateHandle().get<Long>(TripDetailFragment.TRIP_ID_ARG) ?: 0L
            TripDetailViewModel(
                tripId = tripId,
                tripRepository = driveTrackerApplication().container.tripRepository,
            )
        }

        initializer {
            InsightsViewModel(
                tripRepository = driveTrackerApplication().container.tripRepository,
            )
        }

        initializer {
            AuthViewModel(
                authRepository = driveTrackerApplication().container.authRepository,
                tripSyncScheduler = driveTrackerApplication().container.tripSyncScheduler,
            )
        }

        initializer {
            ProfileViewModel(
                authRepository = driveTrackerApplication().container.authRepository,
                tripRepository = driveTrackerApplication().container.tripRepository,
            )
        }
    }
}

fun CreationExtras.driveTrackerApplication(): DriveTrackerApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as DriveTrackerApplication)
