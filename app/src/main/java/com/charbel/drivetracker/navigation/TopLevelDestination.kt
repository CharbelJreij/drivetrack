package com.charbel.drivetracker.navigation

enum class TopLevelDestination(val route: String) {
    Dashboard("dashboard"),
    Record("record"),
    History("history"),
    Insights("insights"),
    Profile("profile"),
}

object AuthDestination {
    const val route = "auth"
}

object TripDetailDestination {
    const val tripIdArg = "tripId"
    const val route = "trip_detail/{$tripIdArg}"

    fun createRoute(tripId: Long): String = "trip_detail/$tripId"
}
