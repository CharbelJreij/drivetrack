package com.charbel.drivetracker.ui.record

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Timelapse
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.charbel.drivetracker.R
import com.charbel.drivetracker.ui.components.EmptyStateCard
import com.charbel.drivetracker.ui.components.GradientHeroCard
import com.charbel.drivetracker.ui.components.HeroActionButton
import com.charbel.drivetracker.ui.components.HeroMiniMetric
import com.charbel.drivetracker.ui.components.InfoCard
import com.charbel.drivetracker.ui.components.MetricCard
import com.charbel.drivetracker.ui.components.RouteStopsCard
import com.charbel.drivetracker.ui.components.SectionTitle
import com.charbel.drivetracker.ui.components.StatusMessageCard
import com.charbel.drivetracker.ui.components.TripRouteMap
import com.charbel.drivetracker.util.formatDistance
import com.charbel.drivetracker.util.formatDuration
import com.charbel.drivetracker.util.formatSpeed
import com.charbel.drivetracker.util.hasLocationPermission
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

@Composable
fun RecordTripScreen(
    uiState: RecordTripUiState,
    savedTripIds: Flow<Long>,
    onStartTracking: () -> Unit,
    onStopTracking: () -> Unit,
    onDismissMessage: () -> Unit,
    onTripSaved: (Long) -> Unit,
) {
    val context = LocalContext.current
    var hasLocationPermission by remember { mutableStateOf(context.hasLocationPermission()) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        hasLocationPermission = granted
        if (granted) {
            onStartTracking()
        }
    }

    LaunchedEffect(savedTripIds) {
        savedTripIds.collect { tripId ->
            onTripSaved(tripId)
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 14.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            GradientHeroCard(
                eyebrow = if (uiState.session.isTracking) {
                    stringResource(R.string.recording_status_active)
                } else {
                    stringResource(R.string.recording_status_idle)
                },
                title = if (uiState.session.isTracking) {
                    stringResource(R.string.record_live_title)
                } else {
                    stringResource(R.string.record_ready_title)
                },
                subtitle = if (uiState.session.isTracking) {
                    stringResource(R.string.record_live_supporting)
                } else {
                    stringResource(R.string.record_ready_supporting)
                },
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    HeroMiniMetric(
                        label = stringResource(R.string.distance),
                        value = formatDistance(uiState.session.distanceMeters),
                        modifier = Modifier.weight(1f),
                    )
                    HeroMiniMetric(
                        label = stringResource(R.string.duration),
                        value = formatDuration(uiState.session.durationSeconds),
                        modifier = Modifier.weight(1f),
                    )
                    HeroMiniMetric(
                        label = stringResource(R.string.avg_speed),
                        value = formatSpeed(uiState.session.averageSpeedKmh),
                        modifier = Modifier.weight(1f),
                    )
                }
                HeroActionButton(
                    text = if (uiState.session.isTracking) {
                        stringResource(R.string.stop_trip)
                    } else {
                        stringResource(R.string.start_trip)
                    },
                    onClick = {
                        hasLocationPermission = context.hasLocationPermission()
                        if (uiState.session.isTracking) {
                            onStopTracking()
                        } else if (hasLocationPermission) {
                            onStartTracking()
                        } else {
                            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    },
                )
            }
        }

        uiState.session.message?.let { message ->
            item {
                StatusMessageCard(message = message)
            }
        }

        if (!hasLocationPermission && !uiState.session.isTracking) {
            item {
                EmptyStateCard(
                    title = stringResource(R.string.location_permission_title),
                    message = stringResource(R.string.location_permission_message),
                    icon = Icons.Outlined.LocationOn,
                )
            }
        }

        item {
            TripRouteMap(
                points = uiState.session.points,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        item {
            RouteStopsCard(
                startLabel = stringResource(R.string.start_address),
                startValue = uiState.session.startAddress ?: stringResource(R.string.address_pending),
                endLabel = stringResource(R.string.end_address),
                endValue = uiState.session.endAddress ?: stringResource(R.string.address_pending),
            )
        }

        item {
            SectionTitle(
                title = stringResource(R.string.live_metrics),
                subtitle = stringResource(R.string.record_subtitle),
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                MetricCard(
                    label = stringResource(R.string.distance),
                    value = formatDistance(uiState.session.distanceMeters),
                    modifier = Modifier.weight(1f),
                    supportingText = stringResource(R.string.metric_distance_supporting),
                )
                MetricCard(
                    label = stringResource(R.string.duration),
                    value = formatDuration(uiState.session.durationSeconds),
                    modifier = Modifier.weight(1f),
                    supportingText = stringResource(R.string.metric_duration_supporting),
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                MetricCard(
                    label = stringResource(R.string.avg_speed),
                    value = formatSpeed(uiState.session.averageSpeedKmh),
                    modifier = Modifier.weight(1f),
                    supportingText = stringResource(R.string.metric_speed_supporting),
                    accentColor = MaterialTheme.colorScheme.secondary,
                )
                MetricCard(
                    label = stringResource(R.string.max_speed),
                    value = formatSpeed(uiState.session.maxSpeedKmh),
                    modifier = Modifier.weight(1f),
                    supportingText = stringResource(R.string.metric_speed_supporting),
                    accentColor = MaterialTheme.colorScheme.tertiary,
                )
            }
        }

        item {
            InfoCard(
                title = stringResource(R.string.offline_ready),
                message = stringResource(R.string.trip_sync_note),
                icon = Icons.Outlined.Timelapse,
                accentColor = MaterialTheme.colorScheme.primary,
            )
        }
    }

    LaunchedEffect(uiState.session.message) {
        if (uiState.session.message != null) {
            delay(2_500L)
            onDismissMessage()
        }
    }
}
