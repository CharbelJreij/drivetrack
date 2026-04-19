package com.charbel.drivetracker.ui.components

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.charbel.drivetracker.R
import com.charbel.drivetracker.model.TripPoint
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

@Composable
fun TripStreetMap(
    points: List<TripPoint>,
    modifier: Modifier = Modifier,
) {
    if (points.isEmpty()) {
        TripRouteMap(
            points = points,
            modifier = modifier,
        )
        return
    }

    val context = LocalContext.current
    val geoPoints = remember(points) {
        points.map { GeoPoint(it.latitude, it.longitude) }
    }
    val routeRenderKey = remember(points) {
        points.hashCode()
    }
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            setUseDataConnection(true)
            minZoomLevel = 3.0
            maxZoomLevel = 19.0
            isTilesScaledToDpi = true
        }
    }

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            SectionTitle(
                title = stringResource(R.string.street_map_title),
                subtitle = stringResource(R.string.street_map_subtitle),
            )
            Spacer(modifier = Modifier.height(14.dp))

            DisposableEffect(mapView) {
                mapView.onResume()
                onDispose {
                    mapView.onPause()
                    mapView.onDetach()
                }
            }

            LaunchedEffect(mapView, routeRenderKey) {
                renderTripOnMap(
                    mapView = mapView,
                    geoPoints = geoPoints,
                )
            }

            AndroidView(
                factory = { mapView },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp),
            )
        }
    }
}

private fun renderTripOnMap(
    mapView: MapView,
    geoPoints: List<GeoPoint>,
) {
    mapView.overlays.clear()

    if (geoPoints.isEmpty()) {
        mapView.invalidate()
        return
    }

    val routeLine = Polyline().apply {
        setPoints(geoPoints)
        outlinePaint.color = AndroidColor.BLACK
        outlinePaint.strokeWidth = 7f
    }
    mapView.overlays.add(routeLine)

    val startMarker = Marker(mapView).apply {
        position = geoPoints.first()
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        title = "Start"
    }
    mapView.overlays.add(startMarker)

    if (geoPoints.size > 1) {
        val endMarker = Marker(mapView).apply {
            position = geoPoints.last()
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "End"
        }
        mapView.overlays.add(endMarker)

        mapView.post {
            mapView.zoomToBoundingBox(
                BoundingBox.fromGeoPointsSafe(geoPoints),
                true,
                64,
            )
        }
    } else {
        mapView.controller.setZoom(16.0)
        mapView.controller.setCenter(geoPoints.first())
    }

    mapView.invalidate()
}
