package com.charbel.drivetracker.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.charbel.drivetracker.R
import com.charbel.drivetracker.model.TripPoint

@Composable
fun TripRouteMap(
    points: List<TripPoint>,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            SectionTitle(
                title = stringResource(R.string.route_overview),
                subtitle = stringResource(R.string.route_preview),
            )
            Spacer(modifier = Modifier.height(14.dp))

            if (points.isEmpty()) {
                MapPlaceholder(text = stringResource(R.string.route_preview))
            } else {
                RouteBoard(points = points)
            }
        }
    }
}

@Composable
private fun RouteBoard(
    points: List<TripPoint>,
) {
    val outlineColor = MaterialTheme.colorScheme.outline
    val surfaceColor = MaterialTheme.colorScheme.surface
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        shape = MaterialTheme.shapes.large,
        color = surfaceVariantColor,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(MaterialTheme.shapes.large)
                .background(surfaceVariantColor),
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val horizontalStep = size.width / 5f
                val verticalStep = size.height / 5f
                val dash = PathEffect.dashPathEffect(floatArrayOf(10.dp.toPx(), 10.dp.toPx()))

                repeat(4) { index ->
                    val x = horizontalStep * (index + 1)
                    val y = verticalStep * (index + 1)
                    drawLine(
                        color = outlineColor.copy(alpha = 0.6f),
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = dash,
                    )
                    drawLine(
                        color = outlineColor.copy(alpha = 0.6f),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = dash,
                    )
                }

                val routePoints = projectRouteOffsets(
                    points = points,
                    width = size.width,
                    height = size.height,
                    padding = 28.dp.toPx(),
                )

                if (routePoints.size == 1) {
                    drawCircle(
                        color = onSurfaceColor,
                        radius = 8.dp.toPx(),
                        center = routePoints.first(),
                    )
                } else if (routePoints.isNotEmpty()) {
                    val routePath = Path().apply {
                        routePoints.forEachIndexed { index, point ->
                            if (index == 0) moveTo(point.x, point.y) else lineTo(point.x, point.y)
                        }
                    }

                    drawPath(
                        path = routePath,
                        color = Color.Black.copy(alpha = 0.08f),
                        style = Stroke(
                            width = 10.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round,
                        ),
                    )
                    drawPath(
                        path = routePath,
                        color = onSurfaceColor,
                        style = Stroke(
                            width = 5.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round,
                        ),
                    )
                    drawCircle(
                        color = onSurfaceColor,
                        radius = 7.dp.toPx(),
                        center = routePoints.first(),
                    )
                    drawCircle(
                        color = secondaryColor,
                        radius = 7.dp.toPx(),
                        center = routePoints.last(),
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(14.dp),
                shape = CircleShape,
                color = surfaceColor,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Map,
                        contentDescription = null,
                        tint = onSurfaceColor,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = stringResource(R.string.route_overview),
                        style = MaterialTheme.typography.labelLarge,
                        color = onSurfaceColor,
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(14.dp),
                shape = MaterialTheme.shapes.medium,
                color = surfaceColor,
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    horizontalAlignment = Alignment.End,
                ) {
                    Text(
                        text = stringResource(R.string.route_points_label),
                        style = MaterialTheme.typography.labelSmall,
                        color = onSurfaceVariantColor,
                    )
                    Text(
                        text = points.size.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = onSurfaceColor,
                    )
                }
            }
        }
    }
}

@Composable
private fun MapPlaceholder(
    text: String,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Map,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = stringResource(R.string.route_overview),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.route_preview_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private fun projectRouteOffsets(
    points: List<TripPoint>,
    width: Float,
    height: Float,
    padding: Float,
): List<Offset> {
    val minLatitude = points.minOfOrNull { it.latitude } ?: return emptyList()
    val maxLatitude = points.maxOfOrNull { it.latitude } ?: return emptyList()
    val minLongitude = points.minOfOrNull { it.longitude } ?: return emptyList()
    val maxLongitude = points.maxOfOrNull { it.longitude } ?: return emptyList()

    val usableWidth = (width - padding * 2f).coerceAtLeast(1f)
    val usableHeight = (height - padding * 2f).coerceAtLeast(1f)
    val latitudeRange = (maxLatitude - minLatitude).takeIf { it > 0.0 } ?: 0.0
    val longitudeRange = (maxLongitude - minLongitude).takeIf { it > 0.0 } ?: 0.0

    return points.map { point ->
        val horizontalRatio = if (longitudeRange == 0.0) {
            0.5f
        } else {
            ((point.longitude - minLongitude) / longitudeRange).toFloat()
        }
        val verticalRatio = if (latitudeRange == 0.0) {
            0.5f
        } else {
            ((point.latitude - minLatitude) / latitudeRange).toFloat()
        }

        Offset(
            x = padding + (horizontalRatio * usableWidth),
            y = height - padding - (verticalRatio * usableHeight),
        )
    }
}
