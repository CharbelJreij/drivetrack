package com.charbel.drivetracker.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.charbel.drivetracker.R
import com.charbel.drivetracker.model.Trip
import com.charbel.drivetracker.util.formatDistance
import com.charbel.drivetracker.util.formatDuration
import com.charbel.drivetracker.util.formatSpeed
import com.charbel.drivetracker.util.formatTripDate

@Composable
fun TripListItem(
    trip: Trip,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline,
        ),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(10.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Route,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text(
                            text = trip.title,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = formatTripDate(trip.startedAtMillis),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    SyncStatusChip(syncStatus = trip.syncStatus)
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Column(
                    modifier = Modifier.padding(top = 2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onSurface),
                    )
                    Box(
                        modifier = Modifier
                            .padding(vertical = 6.dp)
                            .width(2.dp)
                            .height(26.dp)
                            .background(MaterialTheme.colorScheme.outline),
                    )
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary),
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    TripStopText(
                        label = stringResource(R.string.start_address),
                        value = trip.startAddress ?: trip.title,
                    )
                    TripStopText(
                        label = stringResource(R.string.end_address),
                        value = trip.endAddress ?: stringResource(R.string.address_pending),
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                InlineMetricPill(
                    label = stringResource(R.string.distance),
                    value = formatDistance(trip.distanceMeters),
                    modifier = Modifier.weight(1f),
                )
                InlineMetricPill(
                    label = stringResource(R.string.duration),
                    value = formatDuration(trip.durationSeconds),
                    modifier = Modifier.weight(1f),
                )
                InlineMetricPill(
                    label = stringResource(R.string.avg_speed),
                    value = formatSpeed(trip.averageSpeedKmh),
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun TripStopText(
    label: String,
    value: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
