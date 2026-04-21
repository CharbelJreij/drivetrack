package com.charbel.drivetracker.data

import android.content.Context
import androidx.room.Room
import com.charbel.drivetracker.data.auth.AuthLocalDataSource
import com.charbel.drivetracker.data.local.AppDatabase
import com.charbel.drivetracker.data.remote.OpenRouteServiceConfig
import com.charbel.drivetracker.data.remote.OpenRouteServiceFactory
import com.charbel.drivetracker.data.remote.OpenRouteServiceGeocoder
import com.charbel.drivetracker.data.remote.SupabaseConfig
import com.charbel.drivetracker.data.remote.SupabaseServiceFactory
import com.charbel.drivetracker.data.repository.AuthRepository
import com.charbel.drivetracker.data.repository.OfflineFirstTripRepository
import com.charbel.drivetracker.data.repository.SupabaseAuthRepository
import com.charbel.drivetracker.data.repository.TripRepository
import com.charbel.drivetracker.data.sync.SupabaseTripSyncService
import com.charbel.drivetracker.data.sync.TripSyncScheduler
import com.charbel.drivetracker.tracking.TripTrackerManager

class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    private val database: AppDatabase by lazy {
        Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME,
        ).fallbackToDestructiveMigration().build()
    }

    private val syncScheduler: TripSyncScheduler by lazy {
        TripSyncScheduler(appContext)
    }

    private val supabaseConfig: SupabaseConfig by lazy {
        SupabaseConfig.fromBuildConfig()
    }

    private val openRouteServiceConfig: OpenRouteServiceConfig by lazy {
        OpenRouteServiceConfig.fromBuildConfig()
    }

    private val supabaseApi by lazy {
        SupabaseServiceFactory.createApi(supabaseConfig)
    }

    private val openRouteServiceApi by lazy {
        OpenRouteServiceFactory.createApi(openRouteServiceConfig)
    }

    private val authLocalDataSource: AuthLocalDataSource by lazy {
        AuthLocalDataSource(appContext)
    }

    private val openRouteServiceGeocoder: OpenRouteServiceGeocoder by lazy {
        OpenRouteServiceGeocoder(
            api = openRouteServiceApi,
            config = openRouteServiceConfig,
        )
    }

    val authRepository: AuthRepository by lazy {
        SupabaseAuthRepository(
            api = supabaseApi,
            localDataSource = authLocalDataSource,
            config = supabaseConfig,
        )
    }

    val tripRepository: TripRepository by lazy {
        OfflineFirstTripRepository(
            tripDao = database.tripDao(),
            authRepository = authRepository,
            tripSyncService = tripSyncService,
        )
    }

    val tripSyncService: SupabaseTripSyncService by lazy {
        SupabaseTripSyncService(
            api = supabaseApi,
            authRepository = authRepository,
            config = supabaseConfig,
        )
    }

    val tripTrackerManager: TripTrackerManager by lazy {
        TripTrackerManager(
            context = appContext,
            tripRepository = tripRepository,
            syncScheduler = syncScheduler,
            openRouteServiceGeocoder = openRouteServiceGeocoder,
        )
    }

    val tripSyncScheduler: TripSyncScheduler
        get() = syncScheduler
}
