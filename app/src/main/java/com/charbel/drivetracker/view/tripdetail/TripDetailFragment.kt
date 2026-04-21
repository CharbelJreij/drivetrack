package com.charbel.drivetracker.view.tripdetail

import android.os.Bundle
import android.view.MotionEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.charbel.drivetracker.R
import com.charbel.drivetracker.databinding.FragmentTripDetailBinding
import com.charbel.drivetracker.model.SyncStatus
import com.charbel.drivetracker.ui.AppViewModelProvider
import com.charbel.drivetracker.ui.tripdetail.TripDetailViewModel
import com.charbel.drivetracker.util.formatDistance
import com.charbel.drivetracker.util.formatDuration
import com.charbel.drivetracker.util.formatSpeed
import com.charbel.drivetracker.util.formatTripDate
import kotlinx.coroutines.launch
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import android.graphics.Color as AndroidColor

class TripDetailFragment : Fragment() {

    private var _binding: FragmentTripDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TripDetailViewModel by viewModels { AppViewModelProvider.Factory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTripDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
        binding.mapView.setMultiTouchControls(true)
        binding.mapView.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN,
                MotionEvent.ACTION_MOVE -> binding.mapView.parent?.requestDisallowInterceptTouchEvent(true)

                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> binding.mapView.parent?.requestDisallowInterceptTouchEvent(false)
            }
            false
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    val trip = uiState.trip ?: return@collect
                    binding.titleText.text = trip.title
                    binding.dateText.text = formatTripDate(trip.startedAtMillis)
                    binding.startText.text = trip.startAddress ?: getString(R.string.address_pending)
                    binding.endText.text = trip.endAddress ?: getString(R.string.address_pending)
                    binding.distanceValue.text = formatDistance(trip.distanceMeters)
                    binding.durationValue.text = formatDuration(trip.durationSeconds)
                    binding.avgSpeedValue.text = formatSpeed(trip.averageSpeedKmh)
                    binding.maxSpeedValue.text = formatSpeed(trip.maxSpeedKmh)
                    binding.syncChip.text = when (trip.syncStatus) {
                        SyncStatus.PENDING -> getString(R.string.sync_status_pending)
                        SyncStatus.SYNCED -> getString(R.string.sync_status_synced)
                        SyncStatus.FAILED -> getString(R.string.sync_status_failed)
                        SyncStatus.LOCAL_ONLY -> getString(R.string.sync_status_local_only)
                    }
                    binding.syncChip.setChipBackgroundColorResource(
                        when (trip.syncStatus) {
                            SyncStatus.PENDING -> R.color.sync_chip_pending
                            SyncStatus.SYNCED -> R.color.sync_chip_synced
                            SyncStatus.FAILED -> R.color.sync_chip_failed
                            SyncStatus.LOCAL_ONLY -> R.color.sync_chip_local
                        },
                    )
                    binding.syncChip.setTextColor(ContextCompat.getColor(requireContext(), R.color.uber_white))
                    renderMap(trip.points.map { GeoPoint(it.latitude, it.longitude) })
                }
            }
        }
    }

    private fun renderMap(points: List<GeoPoint>) {
        binding.mapView.overlays.clear()
        if (points.isEmpty()) return

        val routeLine = Polyline().apply {
            setPoints(points)
            outlinePaint.color = AndroidColor.BLACK
            outlinePaint.strokeWidth = 7f
        }
        binding.mapView.overlays.add(routeLine)

        binding.mapView.overlays.add(
                Marker(binding.mapView).apply {
                    position = points.first()
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = getString(R.string.start_address)
                },
        )

        if (points.size > 1) {
            binding.mapView.overlays.add(
                Marker(binding.mapView).apply {
                    position = points.last()
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = getString(R.string.end_address)
                },
            )
            binding.mapView.post {
                binding.mapView.zoomToBoundingBox(BoundingBox.fromGeoPointsSafe(points), true, 64)
            }
        } else {
            binding.mapView.controller.setZoom(16.0)
            binding.mapView.controller.setCenter(points.first())
        }
        binding.mapView.invalidate()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        binding.mapView.onPause()
        super.onPause()
    }

    override fun onDestroyView() {
        binding.mapView.onDetach()
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "trip_detail"
        const val TRIP_ID_ARG = "tripId"

        fun newInstance(tripId: Long): TripDetailFragment = TripDetailFragment().apply {
            arguments = Bundle().apply { putLong(TRIP_ID_ARG, tripId) }
        }
    }
}
