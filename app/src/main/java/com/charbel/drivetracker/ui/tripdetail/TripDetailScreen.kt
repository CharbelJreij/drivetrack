package com.charbel.drivetracker.ui.tripdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.charbel.drivetracker.R
import com.charbel.drivetracker.ui.components.EmptyStateCard
import com.charbel.drivetracker.ui.components.GradientHeroCard
import com.charbel.drivetracker.ui.components.HeroMiniMetric
import com.charbel.drivetracker.ui.components.MetricCard
import com.charbel.drivetracker.ui.components.RouteStopsCard
import com.charbel.drivetracker.ui.components.SectionTitle
import com.charbel.drivetracker.ui.components.SyncStatusChip
import com.charbel.drivetracker.ui.components.TripStreetMap
import com.charbel.drivetracker.util.formatDistance
import com.charbel.drivetracker.util.formatDuration
import com.charbel.drivetracker.util.formatSpeed
import com.charbel.drivetracker.util.formatTripDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(
    uiState: TripDetailUiState,
    onBack: () -> Unit,
) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                ),
                title = {
                    Text(text = stringResource(R.string.trip_detail))
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        val trip = uiState.trip
        if (trip == null) {
            EmptyStateCard(
                title = stringResource(R.string.trip_detail),
                message = stringResource(R.string.no_trips_message),
                icon = Icons.Outlined.Route,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
                    .padding(18.dp),
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 18.dp,
                    end = 18.dp,
                    top = innerPadding.calculateTopPadding() + 8.dp,
                    bottom = innerPadding.calculateBottomPadding() + 28.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                item {
                    GradientHeroCard(
                        eyebrow = stringResource(R.string.detail_recorded_drive),
                        title = trip.title,
                        subtitle = formatTripDate(trip.startedAtMillis),
                        trailing = {
                            SyncStatusChip(syncStatus = trip.syncStatus)
                        },
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            HeroMiniMetric(
                                label = stringResource(R.string.distance),
                                value = formatDistance(trip.distanceMeters),
                                modifier = Modifier.weight(1f),
                            )
                            HeroMiniMetric(
                                label = stringResource(R.string.duration),
                                value = formatDuration(trip.durationSeconds),
                                modifier = Modifier.weight(1f),
                            )
                            HeroMiniMetric(
                                label = stringResource(R.string.avg_speed),
                                value = formatSpeed(trip.averageSpeedKmh),
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }

                item {
                    TripStreetMap(
                        points = trip.points,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                item {
                    RouteStopsCard(
                        startLabel = stringResource(R.string.start_address),
                        startValue = trip.startAddress ?: stringResource(R.string.address_pending),
                        endLabel = stringResource(R.string.end_address),
                        endValue = trip.endAddress ?: stringResource(R.string.address_pending),
                    )
                }

                item {
                    SectionTitle(
                        title = stringResource(R.string.trip_summary),
                        subtitle = stringResource(R.string.detail_recorded_drive),
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        MetricCard(
                            label = stringResource(R.string.distance),
                            value = formatDistance(trip.distanceMeters),
                            modifier = Modifier.weight(1f),
                            supportingText = stringResource(R.string.metric_distance_supporting),
                        )
                        MetricCard(
                            label = stringResource(R.string.duration),
                            value = formatDuration(trip.durationSeconds),
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
                            value = formatSpeed(trip.averageSpeedKmh),
                            modifier = Modifier.weight(1f),
                            supportingText = stringResource(R.string.metric_speed_supporting),
                            accentColor = MaterialTheme.colorScheme.secondary,
                        )
                        MetricCard(
                            label = stringResource(R.string.max_speed),
                            value = formatSpeed(trip.maxSpeedKmh),
                            modifier = Modifier.weight(1f),
                            supportingText = stringResource(R.string.metric_speed_supporting),
                            accentColor = MaterialTheme.colorScheme.tertiary,
                        )
                    }
                }
            }
        }
    }
}
