package com.charbel.drivetracker.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.charbel.drivetracker.R
import com.charbel.drivetracker.ui.components.ConnectionStatusPill
import com.charbel.drivetracker.ui.components.EmptyStateCard
import com.charbel.drivetracker.ui.components.GradientHeroCard
import com.charbel.drivetracker.ui.components.HeroActionButton
import com.charbel.drivetracker.ui.components.HeroMiniMetric
import com.charbel.drivetracker.ui.components.InfoCard
import com.charbel.drivetracker.ui.components.MetricCard
import com.charbel.drivetracker.ui.components.RouteStopsCard
import com.charbel.drivetracker.ui.components.SectionTitle
import com.charbel.drivetracker.ui.components.StatusMessageCard
import com.charbel.drivetracker.ui.components.TripListItem
import com.charbel.drivetracker.ui.components.TripRouteMap
import com.charbel.drivetracker.util.formatDistance
import com.charbel.drivetracker.util.formatDuration
import com.charbel.drivetracker.util.formatSpeed
import kotlinx.coroutines.delay

@Composable
fun DashboardScreen(
    uiState: HomeUiState,
    onOpenHistory: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenRecorder: () -> Unit,
    onOpenTrip: (Long) -> Unit,
    onDismissMessage: () -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 14.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            GradientHeroCard(
                eyebrow = if (uiState.trackingSession.isTracking) {
                    stringResource(R.string.recording_status_active)
                } else {
                    stringResource(R.string.recording_status_idle)
                },
                title = stringResource(R.string.dashboard_hero_title),
                subtitle = stringResource(R.string.dashboard_subtitle),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    HeroMiniMetric(
                        label = stringResource(R.string.distance),
                        value = formatDistance(uiState.weeklySummary.totalDistanceMeters),
                        modifier = Modifier.weight(1f),
                    )
                    HeroMiniMetric(
                        label = stringResource(R.string.duration),
                        value = formatDuration(uiState.weeklySummary.totalDurationSeconds),
                        modifier = Modifier.weight(1f),
                    )
                    HeroMiniMetric(
                        label = stringResource(R.string.stats_number_of_drives),
                        value = uiState.weeklySummary.driveCount.toString(),
                        modifier = Modifier.weight(1f),
                    )
                }
                HeroActionButton(
                    text = if (uiState.trackingSession.isTracking) {
                        stringResource(R.string.record_trip)
                    } else {
                        stringResource(R.string.start_trip)
                    },
                    onClick = onOpenRecorder,
                )
            }
        }

        uiState.trackingSession.message?.let { message ->
            item {
                StatusMessageCard(message = message)
            }
        }

        item {
            SectionTitle(
                title = stringResource(R.string.connection),
                subtitle = uiState.accountEmail ?: stringResource(R.string.profile_not_signed_in),
                trailing = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ConnectionStatusPill()
                        TextButton(onClick = onOpenProfile) {
                            Text(text = stringResource(R.string.profile))
                        }
                    }
                },
            )
        }

        item {
            SectionTitle(
                title = stringResource(R.string.live_trip),
                subtitle = stringResource(R.string.active_trip_summary),
            )
        }

        if (uiState.trackingSession.isTracking) {
            item {
                TripRouteMap(
                    points = uiState.trackingSession.points,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            item {
                RouteStopsCard(
                    startLabel = stringResource(R.string.start_address),
                    startValue = uiState.trackingSession.startAddress ?: stringResource(R.string.address_pending),
                    endLabel = stringResource(R.string.end_address),
                    endValue = uiState.trackingSession.endAddress ?: stringResource(R.string.address_pending),
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    MetricCard(
                        label = stringResource(R.string.distance),
                        value = formatDistance(uiState.trackingSession.distanceMeters),
                        modifier = Modifier.weight(1f),
                        supportingText = stringResource(R.string.metric_distance_supporting),
                    )
                    MetricCard(
                        label = stringResource(R.string.avg_speed),
                        value = formatSpeed(uiState.trackingSession.averageSpeedKmh),
                        modifier = Modifier.weight(1f),
                        supportingText = stringResource(R.string.metric_speed_supporting),
                        accentColor = MaterialTheme.colorScheme.secondary,
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    MetricCard(
                        label = stringResource(R.string.duration),
                        value = formatDuration(uiState.trackingSession.durationSeconds),
                        modifier = Modifier.weight(1f),
                        supportingText = stringResource(R.string.metric_duration_supporting),
                    )
                    MetricCard(
                        label = stringResource(R.string.max_speed),
                        value = formatSpeed(uiState.trackingSession.maxSpeedKmh),
                        modifier = Modifier.weight(1f),
                        supportingText = stringResource(R.string.metric_speed_supporting),
                        accentColor = MaterialTheme.colorScheme.tertiary,
                    )
                }
            }
        } else {
            item {
                InfoCard(
                    title = stringResource(R.string.dashboard_idle_title),
                    message = stringResource(R.string.tracking_inactive),
                    icon = Icons.Outlined.DirectionsCar,
                    accentColor = MaterialTheme.colorScheme.primary,
                )
            }
            item {
                InfoCard(
                    title = stringResource(R.string.route_overview),
                    message = stringResource(R.string.dashboard_idle_map_message),
                    icon = Icons.Outlined.Map,
                    accentColor = MaterialTheme.colorScheme.secondary,
                )
            }
        }

        item {
            SectionTitle(
                title = stringResource(R.string.recent_trips),
                subtitle = stringResource(R.string.trip_sync_note),
                trailing = {
                    TextButton(onClick = onOpenHistory) {
                        Text(text = stringResource(R.string.view_all))
                    }
                },
            )
        }

        if (uiState.recentTrips.isEmpty()) {
            item {
                EmptyStateCard(
                    title = stringResource(R.string.no_trips_title),
                    message = stringResource(R.string.no_trips_message),
                    icon = Icons.Outlined.History,
                )
            }
        } else {
            items(uiState.recentTrips.take(3), key = { trip -> trip.id }) { trip ->
                TripListItem(
                    trip = trip,
                    onClick = { onOpenTrip(trip.id) },
                )
            }
        }
    }

    LaunchedEffect(uiState.trackingSession.message) {
        if (uiState.trackingSession.message != null) {
            delay(2_500L)
            onDismissMessage()
        }
    }
}
