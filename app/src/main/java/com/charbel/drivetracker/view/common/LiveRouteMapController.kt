package com.charbel.drivetracker.view.common

import android.graphics.Color as AndroidColor
import com.charbel.drivetracker.R
import com.charbel.drivetracker.model.TripPoint
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

class LiveRouteMapController(
    private val mapView: MapView,
) {

    private val routeLine = Polyline().apply {
        outlinePaint.color = AndroidColor.BLACK
        outlinePaint.strokeWidth = 7f
    }

    private val startMarker = Marker(mapView).apply {
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        title = mapView.context.getString(R.string.start_address)
    }

    private val endMarker = Marker(mapView).apply {
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        title = mapView.context.getString(R.string.end_address)
    }

    private var lastRenderedSignature: Pair<Int, Long>? = null

    init {
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(false)
        mapView.isTilesScaledToDpi = true
        mapView.setBuiltInZoomControls(false)
        mapView.controller.setZoom(15.0)
    }

    fun render(points: List<TripPoint>) {
        if (points.isEmpty()) {
            clear()
            return
        }

        val signature = points.size to points.last().recordedAtMillis
        if (signature == lastRenderedSignature) return
        lastRenderedSignature = signature

        val geoPoints = points.map { GeoPoint(it.latitude, it.longitude) }
        routeLine.setPoints(geoPoints)

        if (!mapView.overlays.contains(routeLine)) {
            mapView.overlays.add(routeLine)
        }

        startMarker.position = geoPoints.first()
        if (!mapView.overlays.contains(startMarker)) {
            mapView.overlays.add(startMarker)
        }

        if (geoPoints.size > 1) {
            endMarker.position = geoPoints.last()
            if (!mapView.overlays.contains(endMarker)) {
                mapView.overlays.add(endMarker)
            }
            mapView.post {
                mapView.zoomToBoundingBox(BoundingBox.fromGeoPointsSafe(geoPoints), false, 64)
            }
        } else {
            mapView.overlays.remove(endMarker)
            mapView.controller.setCenter(geoPoints.first())
            mapView.controller.setZoom(16.0)
        }

        mapView.invalidate()
    }

    fun clear() {
        if (lastRenderedSignature == null && mapView.overlays.isEmpty()) return
        lastRenderedSignature = null
        mapView.overlays.clear()
        mapView.invalidate()
    }
}
