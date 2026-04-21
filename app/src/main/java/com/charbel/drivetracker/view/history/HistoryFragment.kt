package com.charbel.drivetracker.view.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.charbel.drivetracker.MainActivity
import com.charbel.drivetracker.databinding.FragmentHistoryBinding
import com.charbel.drivetracker.ui.AppViewModelProvider
import com.charbel.drivetracker.ui.history.HistoryViewModel
import com.charbel.drivetracker.view.common.TripListAdapter
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HistoryViewModel by viewModels { AppViewModelProvider.Factory }
    private val adapter = TripListAdapter { trip ->
        (activity as? MainActivity)?.openTripDetail(trip.id)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.historyList.layoutManager = LinearLayoutManager(requireContext())
        binding.historyList.adapter = adapter
        binding.searchInput.doAfterTextChanged {
            viewModel.updateQuery(it?.toString().orEmpty())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    if (binding.searchInput.text?.toString() != uiState.query) {
                        binding.searchInput.setText(uiState.query)
                    }
                    adapter.submitList(uiState.trips)
                    binding.emptyText.isVisible = uiState.trips.isEmpty()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "history"
    }
}
