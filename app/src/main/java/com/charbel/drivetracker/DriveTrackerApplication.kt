package com.charbel.drivetracker

import android.app.Application
import androidx.work.Configuration
import com.charbel.drivetracker.data.AppContainer
import org.osmdroid.config.Configuration as OsmdroidConfiguration

class DriveTrackerApplication : Application(), Configuration.Provider {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        OsmdroidConfiguration.getInstance().load(
            this,
            getSharedPreferences("osmdroid_prefs", MODE_PRIVATE),
        )
        OsmdroidConfiguration.getInstance().userAgentValue = packageName
        container = AppContainer(this)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().build()
}
