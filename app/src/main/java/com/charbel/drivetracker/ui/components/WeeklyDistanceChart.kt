package com.charbel.drivetracker.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.charbel.drivetracker.R
import com.charbel.drivetracker.model.WeeklyDistanceBar
import com.charbel.drivetracker.util.formatDistance
import kotlin.math.roundToInt

@Composable
fun WeeklyDistanceChart(
    bars: List<WeeklyDistanceBar>,
    modifier: Modifier = Modifier,
) {
    val maxDistance = (bars.maxOfOrNull { it.distanceMeters } ?: 0.0).coerceAtLeast(1.0)

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Text(
                text = stringResource(R.string.last_six_weeks).uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                bars.forEachIndexed { index, bar ->
                    val height = ((bar.distanceMeters / maxDistance) * 138).roundToInt().coerceAtLeast(10)
                    val isLatest = index == bars.lastIndex
                    val barColor = if (isLatest) {
                        MaterialTheme.colorScheme.secondary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f)
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = formatDistance(bar.distanceMeters),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height(146.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(18.dp),
                                ),
                            contentAlignment = Alignment.BottomCenter,
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(height.dp)
                                    .background(
                                        color = barColor,
                                        shape = RoundedCornerShape(18.dp),
                                    ),
                            )
                        }
                        Text(
                            text = bar.label,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
        }
    }
}
