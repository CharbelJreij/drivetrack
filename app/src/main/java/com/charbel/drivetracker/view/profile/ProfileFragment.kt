package com.charbel.drivetracker.view.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.charbel.drivetracker.R
import com.charbel.drivetracker.databinding.FragmentProfileBinding
import com.charbel.drivetracker.ui.AppViewModelProvider
import com.charbel.drivetracker.ui.profile.ProfileViewModel
import com.charbel.drivetracker.view.common.observeConnectionStatus
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels { AppViewModelProvider.Factory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signOutButton.setOnClickListener { viewModel.signOut() }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    viewModel.uiState,
                    requireContext().observeConnectionStatus(),
                ) { uiState, isOnline -> uiState to isOnline }
                    .collect { (uiState, isOnline) ->
                        binding.emailText.text = uiState.email ?: getString(R.string.profile_not_signed_in)

                        val syncTitle = when {
                            uiState.pendingTripCount > 0 && !isOnline -> getString(R.string.profile_sync_awaiting_title)
                            uiState.pendingTripCount > 0 -> getString(R.string.profile_sync_in_progress_title)
                            isOnline -> getString(R.string.profile_sync_complete_title)
                            else -> getString(R.string.offline)
                        }

                        val syncMessage = when {
                            uiState.pendingTripCount > 0 && !isOnline -> getString(
                                R.string.profile_sync_awaiting_message,
                                uiState.pendingTripCount,
                            )
                            uiState.pendingTripCount > 0 -> getString(
                                R.string.profile_sync_in_progress_message,
                                uiState.pendingTripCount,
                            )
                            isOnline -> getString(R.string.profile_sync_complete_message)
                            else -> getString(R.string.profile_sync_offline_message)
                        }

                        binding.syncTitleText.text = syncTitle
                        binding.syncMessageText.text = syncMessage
                    }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "profile"
    }
}
