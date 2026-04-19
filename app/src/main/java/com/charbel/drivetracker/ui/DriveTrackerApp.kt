package com.charbel.drivetracker.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.charbel.drivetracker.DriveTrackerApplication
import com.charbel.drivetracker.R
import com.charbel.drivetracker.navigation.AuthDestination
import com.charbel.drivetracker.navigation.DriveTrackerNavGraph
import com.charbel.drivetracker.navigation.TopLevelDestination

@Composable
fun DriveTrackerApp() {
    val context = LocalContext.current
    val app = remember(context) { context.applicationContext as DriveTrackerApplication }
    val authSession by app.container.authRepository.session.collectAsStateWithLifecycle()
    val isAuthenticated = authSession != null
    key(isAuthenticated) {
        val navController = rememberNavController()
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = backStackEntry?.destination
        val topLevelDestinations = TopLevelDestination.entries
        val showBottomBar = isAuthenticated && topLevelDestinations.any { destination ->
            currentDestination?.hierarchy?.any { navDestination ->
                navDestination.route == destination.route
            } == true
        }
        val startDestination = if (isAuthenticated) {
            TopLevelDestination.Dashboard.route
        } else {
            AuthDestination.route
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar(
                        modifier = Modifier.clip(MaterialTheme.shapes.large),
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 0.dp,
                    ) {
                        topLevelDestinations.forEach { destination ->
                            val isSelected = currentDestination
                                ?.hierarchy
                                ?.any { navDestination -> navDestination.route == destination.route } == true

                            NavigationBarItem(
                                selected = isSelected,
                                onClick = {
                                    navController.navigate(destination.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                                    selectedIconColor = MaterialTheme.colorScheme.onSurface,
                                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                ),
                                icon = {
                                    Icon(
                                        imageVector = when (destination) {
                                            TopLevelDestination.Dashboard -> Icons.Outlined.Dashboard
                                            TopLevelDestination.Record -> Icons.Outlined.DirectionsCar
                                            TopLevelDestination.History -> Icons.Outlined.History
                                            TopLevelDestination.Insights -> Icons.Outlined.Analytics
                                            TopLevelDestination.Profile -> Icons.Outlined.Person
                                        },
                                        contentDescription = null,
                                    )
                                },
                                label = {
                                    Text(
                                        text = when (destination) {
                                            TopLevelDestination.Dashboard -> stringResource(R.string.dashboard)
                                            TopLevelDestination.Record -> stringResource(R.string.record_trip)
                                            TopLevelDestination.History -> stringResource(R.string.trip_history)
                                            TopLevelDestination.Insights -> stringResource(R.string.insights)
                                            TopLevelDestination.Profile -> stringResource(R.string.profile)
                                        },
                                    )
                                },
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            DriveTrackerNavGraph(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
                startDestination = startDestination,
            )
        }
    }
}
