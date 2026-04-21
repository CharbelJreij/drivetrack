package com.charbel.drivetracker.view.insights

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.charbel.drivetracker.databinding.FragmentInsightsBinding
import com.charbel.drivetracker.ui.AppViewModelProvider
import com.charbel.drivetracker.ui.insights.InsightsViewModel
import com.charbel.drivetracker.util.formatDistance
import com.charbel.drivetracker.util.formatDuration
import kotlinx.coroutines.launch

class InsightsFragment : Fragment() {

    private var _binding: FragmentInsightsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InsightsViewModel by viewModels { AppViewModelProvider.Factory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentInsightsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    binding.chartView.setBars(uiState.weeklyBars)
                    binding.weekDistanceValue.text = formatDistance(uiState.weeklySummary.totalDistanceMeters)
                    binding.weekDriveCountValue.text = uiState.weeklySummary.driveCount.toString()
                    binding.weekDurationValue.text = formatDuration(uiState.weeklySummary.totalDurationSeconds)
                    binding.monthDistanceValue.text = formatDistance(uiState.monthlySummary.totalDistanceMeters)
                    binding.monthDriveCountValue.text = uiState.monthlySummary.driveCount.toString()
                    binding.monthDurationValue.text = formatDuration(uiState.monthlySummary.totalDurationSeconds)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "insights"
    }
}
