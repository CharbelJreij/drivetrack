package com.charbel.drivetracker.view.auth

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
import com.charbel.drivetracker.R
import com.charbel.drivetracker.databinding.FragmentCreateAccountBinding
import com.charbel.drivetracker.ui.AppViewModelProvider
import com.charbel.drivetracker.ui.auth.AuthViewModel
import kotlinx.coroutines.launch

class CreateAccountFragment : Fragment() {

    private var _binding: FragmentCreateAccountBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels { AppViewModelProvider.Factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCreateAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
        binding.emailInput.doAfterTextChanged { viewModel.updateEmail(it?.toString().orEmpty()) }
        binding.passwordInput.doAfterTextChanged { viewModel.updatePassword(it?.toString().orEmpty()) }
        binding.createAccountButton.setOnClickListener { viewModel.signUp() }
        binding.openSignInButton.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    binding.createAccountButton.text = if (uiState.isLoading) {
                        getString(R.string.auth_working)
                    } else {
                        getString(R.string.auth_create_account)
                    }
                    binding.createAccountButton.isEnabled = !uiState.isLoading && uiState.isConfigured
                    binding.openSignInButton.isEnabled = !uiState.isLoading
                    binding.emailInput.isEnabled = !uiState.isLoading
                    binding.passwordInput.isEnabled = !uiState.isLoading
                    binding.loadingIndicator.isVisible = uiState.isLoading
                    binding.messageText.isVisible = !uiState.message.isNullOrBlank()
                    binding.messageText.text = uiState.message
                    binding.configText.isVisible = !uiState.isConfigured
                    binding.configText.text = getString(R.string.auth_missing_config_message)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "create_account"
    }
}
