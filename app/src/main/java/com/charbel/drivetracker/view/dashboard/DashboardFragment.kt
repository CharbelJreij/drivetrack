package com.charbel.drivetracker.view.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.charbel.drivetracker.MainActivity
import com.charbel.drivetracker.R
import com.charbel.drivetracker.databinding.FragmentDashboardBinding
import com.charbel.drivetracker.ui.AppViewModelProvider
import com.charbel.drivetracker.ui.dashboard.HomeViewModel
import com.charbel.drivetracker.util.formatDistance
import com.charbel.drivetracker.util.formatDuration
import com.charbel.drivetracker.util.formatSpeed
import com.charbel.drivetracker.view.common.LiveRouteMapController
import com.charbel.drivetracker.view.common.TripListAdapter
import com.charbel.drivetracker.view.common.observeConnectionStatus
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels { AppViewModelProvider.Factory }
    private val tripAdapter = TripListAdapter { trip ->
        (activity as? MainActivity)?.openTripDetail(trip.id)
    }
    private var liveRouteMapController: LiveRouteMapController? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        liveRouteMapController = LiveRouteMapController(binding.routeBoard)
        binding.recentTripsList.layoutManager = LinearLayoutManager(requireContext())
        binding.recentTripsList.adapter = tripAdapter
        binding.openRecordButton.setOnClickListener {
            (activity as? MainActivity)?.findViewById<View>(R.id.bottom_navigation)?.let {
                (activity as? MainActivity)?.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_navigation)
                    ?.selectedItemId = R.id.menu_record
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { uiState ->
                        binding.statusChip.text = getString(
                            if (uiState.trackingSession.isTracking) {
                                R.string.recording_status_active
                            } else {
                                R.string.recording_status_idle
                            },
                        )
                        binding.openRecordButton.text = getString(
                            if (uiState.trackingSession.isTracking) {
                                R.string.record_trip
                            } else {
                                R.string.start_trip
                            },
                        )
                        binding.weeklyDistanceValue.text = formatDistance(uiState.weeklySummary.totalDistanceMeters)
                        binding.weeklyDurationValue.text = formatDuration(uiState.weeklySummary.totalDurationSeconds)
                        binding.weeklyDrivesValue.text = uiState.weeklySummary.driveCount.toString()
                        binding.accountEmailText.text = uiState.accountEmail ?: getString(R.string.profile_not_signed_in)

                        if (uiState.trackingSession.isTracking) {
                            binding.liveMessageText.text = getString(R.string.active_trip_summary)
                            liveRouteMapController?.render(uiState.trackingSession.points)
                            binding.liveDistanceValue.text = formatDistance(uiState.trackingSession.distanceMeters)
                            binding.liveDurationValue.text = formatDuration(uiState.trackingSession.durationSeconds)
                            binding.liveSpeedValue.text = formatSpeed(uiState.trackingSession.averageSpeedKmh)
                        } else {
                            binding.liveMessageText.text = getString(R.string.tracking_inactive)
                            liveRouteMapController?.clear()
                            binding.liveDistanceValue.text = formatDistance(0.0)
                            binding.liveDurationValue.text = formatDuration(0L)
                            binding.liveSpeedValue.text = formatSpeed(0.0)
                        }

                        tripAdapter.submitList(uiState.recentTrips.take(3))
                        binding.recentEmptyText.isVisible = uiState.recentTrips.isEmpty()
                    }
                }

                launch {
                    requireContext().observeConnectionStatus().collect { isOnline ->
                        binding.connectionChip.text = getString(if (isOnline) R.string.online else R.string.offline)
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
        const val TAG = "dashboard"
    }
}
