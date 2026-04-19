package com.charbel.drivetracker.ui.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.charbel.drivetracker.R
import com.charbel.drivetracker.ui.components.EmptyStateCard
import com.charbel.drivetracker.ui.components.GradientHeroCard
import com.charbel.drivetracker.ui.components.HeroMiniMetric
import com.charbel.drivetracker.ui.components.SectionTitle
import com.charbel.drivetracker.ui.components.TripListItem

@Composable
fun HistoryScreen(
    uiState: HistoryUiState,
    onQueryChange: (String) -> Unit,
    onOpenTrip: (Long) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 14.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            GradientHeroCard(
                eyebrow = stringResource(R.string.trip_history),
                title = stringResource(R.string.history_hero_title),
                subtitle = stringResource(R.string.history_subtitle),
            ) {
                HeroMiniMetric(
                    label = stringResource(R.string.trip_history),
                    value = stringResource(R.string.history_count, uiState.trips.size),
                )
            }
        }

        item {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.9f),
                ),
            ) {
                OutlinedTextField(
                    value = uiState.query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null,
                        )
                    },
                    placeholder = {
                        Text(text = stringResource(R.string.search_trips))
                    },
                    supportingText = {
                        Text(text = stringResource(R.string.search_helper))
                    },
                    singleLine = true,
                    shape = MaterialTheme.shapes.large,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent,
                        disabledBorderColor = Color.Transparent,
                    ),
                )
            }
        }

        item {
            SectionTitle(
                title = stringResource(R.string.recent_trips),
                subtitle = stringResource(R.string.history_count, uiState.trips.size),
            )
        }

        if (uiState.trips.isEmpty()) {
            item {
                EmptyStateCard(
                    title = if (uiState.query.isBlank()) {
                        stringResource(R.string.no_trips_title)
                    } else {
                        stringResource(R.string.history_empty_search)
                    },
                    message = if (uiState.query.isBlank()) {
                        stringResource(R.string.no_trips_message)
                    } else {
                        stringResource(R.string.history_subtitle)
                    },
                    icon = Icons.Outlined.History,
                )
            }
        } else {
            items(uiState.trips, key = { trip -> trip.id }) { trip ->
                TripListItem(
                    trip = trip,
                    onClick = { onOpenTrip(trip.id) },
                )
            }
        }
    }
}
