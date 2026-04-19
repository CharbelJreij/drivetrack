package com.charbel.drivetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.charbel.drivetracker.ui.DriveTrackerApp
import com.charbel.drivetracker.ui.theme.DriveTrackerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DriveTrackerTheme {
                DriveTrackerApp()
            }
        }
    }
}

