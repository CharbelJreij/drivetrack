package com.charbel.drivetracker.view.record

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.charbel.drivetracker.MainActivity
import com.charbel.drivetracker.R
import com.charbel.drivetracker.databinding.FragmentRecordTripBinding
import com.charbel.drivetracker.ui.AppViewModelProvider
import com.charbel.drivetracker.ui.record.RecordTripViewModel
import com.charbel.drivetracker.util.formatDistance
import com.charbel.drivetracker.util.formatDuration
import com.charbel.drivetracker.util.formatSpeed
import com.charbel.drivetracker.util.hasLocationPermission
import com.charbel.drivetracker.view.common.LiveRouteMapController
import kotlinx.coroutines.launch

class RecordTripFragment : Fragment() {

    private var _binding: FragmentRecordTripBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RecordTripViewModel by viewModels { AppViewModelProvider.Factory }
    private var liveRouteMapController: LiveRouteMapController? = null

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) viewModel.startTracking()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRecordTripBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        liveRouteMapController = LiveRouteMapController(binding.routeBoard)
        binding.toggleTrackingButton.setOnClickListener {
            if (binding.toggleTrackingButton.text == getString(R.string.stop_trip)) {
                viewModel.stopTracking()
            } else if (requireContext().hasLocationPermission()) {
                viewModel.startTracking()
            } else {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { uiState ->
                        binding.statusChip.text = getString(
                            if (uiState.session.isTracking) {
                                R.string.recording_status_active
                            } else {
                                R.string.recording_status_idle
                            },
                        )
                        binding.titleText.text = getString(
                            if (uiState.session.isTracking) {
                                R.string.record_live_title
                            } else {
                                R.string.record_ready_title
                            },
                        )
                        binding.subtitleText.text = getString(
                            if (uiState.session.isTracking) {
                                R.string.record_live_supporting
                            } else {
                                R.string.record_ready_supporting
                            },
                        )
                        binding.toggleTrackingButton.text = getString(
                            if (uiState.session.isTracking) {
                                R.string.stop_trip
                            } else {
                                R.string.start_trip
                            },
                        )
                        binding.distanceValue.text = formatDistance(uiState.session.distanceMeters)
                        binding.durationValue.text = formatDuration(uiState.session.durationSeconds)
                        binding.avgSpeedValue.text = formatSpeed(uiState.session.averageSpeedKmh)
                        binding.metricDistanceText.text = formatDistance(uiState.session.distanceMeters)
                        binding.metricDurationText.text = formatDuration(uiState.session.durationSeconds)
                        binding.metricAvgSpeedText.text = formatSpeed(uiState.session.averageSpeedKmh)
                        binding.metricMaxSpeedText.text = formatSpeed(uiState.session.maxSpeedKmh)
                        liveRouteMapController?.render(uiState.session.points)
                        binding.startAddressText.text = uiState.session.startAddress ?: getString(R.string.address_pending)
                        binding.endAddressText.text = uiState.session.endAddress ?: getString(R.string.address_pending)
                        binding.messageText.isVisible = !uiState.session.message.isNullOrBlank()
                        binding.messageText.text = uiState.session.message
                    }
                }

                launch {
                    viewModel.savedTripIds.collect { tripId ->
                        (activity as? MainActivity)?.openTripDetail(tripId)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.routeBoard.onResume()
    }

    override fun onPause() {
        binding.routeBoard.onPause()
        super.onPause()
    }

    override fun onDestroyView() {
        binding.routeBoard.onDetach()
        liveRouteMapController = null
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "record"
    }
}
