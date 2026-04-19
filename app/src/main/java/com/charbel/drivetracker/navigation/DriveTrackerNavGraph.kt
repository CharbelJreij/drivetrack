package com.charbel.drivetracker.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.charbel.drivetracker.ui.AppViewModelProvider
import com.charbel.drivetracker.ui.auth.AuthScreen
import com.charbel.drivetracker.ui.auth.AuthViewModel
import com.charbel.drivetracker.ui.dashboard.DashboardScreen
import com.charbel.drivetracker.ui.dashboard.HomeViewModel
import com.charbel.drivetracker.ui.history.HistoryScreen
import com.charbel.drivetracker.ui.history.HistoryViewModel
import com.charbel.drivetracker.ui.insights.InsightsScreen
import com.charbel.drivetracker.ui.insights.InsightsViewModel
import com.charbel.drivetracker.ui.profile.ProfileScreen
import com.charbel.drivetracker.ui.profile.ProfileViewModel
import com.charbel.drivetracker.ui.record.RecordTripScreen
import com.charbel.drivetracker.ui.record.RecordTripViewModel
import com.charbel.drivetracker.ui.tripdetail.TripDetailScreen
import com.charbel.drivetracker.ui.tripdetail.TripDetailViewModel

@Composable
fun DriveTrackerNavGraph(
    modifier: Modifier = Modifier,
    navController: androidx.navigation.NavHostController = rememberNavController(),
    startDestination: String = AuthDestination.route,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(AuthDestination.route) {
            val viewModel: AuthViewModel = viewModel(factory = AppViewModelProvider.Factory)
            val uiState = viewModel.uiState.collectAsStateWithLifecycle()
            AuthScreen(
                uiState = uiState.value,
                onEmailChange = viewModel::updateEmail,
                onPasswordChange = viewModel::updatePassword,
                onModeChange = viewModel::setMode,
                onSubmit = viewModel::submit,
                onDismissMessage = viewModel::dismissMessage,
            )
        }

        composable(TopLevelDestination.Dashboard.route) {
            val viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
            val uiState = viewModel.uiState.collectAsStateWithLifecycle()
            DashboardScreen(
                uiState = uiState.value,
                onOpenHistory = {
                    navController.navigate(TopLevelDestination.History.route)
                },
                onOpenProfile = {
                    navController.navigate(TopLevelDestination.Profile.route)
                },
                onOpenRecorder = {
                    navController.navigate(TopLevelDestination.Record.route)
                },
                onOpenTrip = { tripId ->
                    navController.navigate(TripDetailDestination.createRoute(tripId))
                },
                onDismissMessage = viewModel::dismissMessage,
            )
        }

        composable(TopLevelDestination.Record.route) {
            val viewModel: RecordTripViewModel = viewModel(factory = AppViewModelProvider.Factory)
            val uiState = viewModel.uiState.collectAsStateWithLifecycle()
            RecordTripScreen(
                uiState = uiState.value,
                savedTripIds = viewModel.savedTripIds,
                onStartTracking = viewModel::startTracking,
                onStopTracking = viewModel::stopTracking,
                onDismissMessage = viewModel::dismissMessage,
                onTripSaved = { tripId ->
                    navController.navigate(TripDetailDestination.createRoute(tripId))
                },
            )
        }

        composable(TopLevelDestination.History.route) {
            val viewModel: HistoryViewModel = viewModel(factory = AppViewModelProvider.Factory)
            val uiState = viewModel.uiState.collectAsStateWithLifecycle()
            HistoryScreen(
                uiState = uiState.value,
                onQueryChange = viewModel::updateQuery,
                onOpenTrip = { tripId ->
                    navController.navigate(TripDetailDestination.createRoute(tripId))
                },
            )
        }

        composable(TopLevelDestination.Insights.route) {
            val viewModel: InsightsViewModel = viewModel(factory = AppViewModelProvider.Factory)
            val uiState = viewModel.uiState.collectAsStateWithLifecycle()
            InsightsScreen(uiState = uiState.value)
        }

        composable(TopLevelDestination.Profile.route) {
            val viewModel: ProfileViewModel = viewModel(factory = AppViewModelProvider.Factory)
            val uiState = viewModel.uiState.collectAsStateWithLifecycle()
            ProfileScreen(
                uiState = uiState.value,
                onSignOut = viewModel::signOut,
            )
        }

        composable(
            route = TripDetailDestination.route,
            arguments = listOf(navArgument(TripDetailDestination.tripIdArg) { type = NavType.LongType }),
        ) {
            val viewModel: TripDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
            val uiState = viewModel.uiState.collectAsStateWithLifecycle()
            TripDetailScreen(
                uiState = uiState.value,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
