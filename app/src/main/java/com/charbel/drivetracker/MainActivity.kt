package com.charbel.drivetracker

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.charbel.drivetracker.databinding.ActivityMainBinding
import com.charbel.drivetracker.view.auth.AuthFragment
import com.charbel.drivetracker.view.auth.CreateAccountFragment
import com.charbel.drivetracker.view.dashboard.DashboardFragment
import com.charbel.drivetracker.view.history.HistoryFragment
import com.charbel.drivetracker.view.insights.InsightsFragment
import com.charbel.drivetracker.view.profile.ProfileFragment
import com.charbel.drivetracker.view.record.RecordTripFragment
import com.charbel.drivetracker.view.tripdetail.TripDetailFragment
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var lastRemoteRefreshUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.fragmentContainer.setPadding(0, systemBars.top, 0, 0)
            binding.bottomNavigation.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_dashboard -> showTopLevelFragment(DashboardFragment(), DashboardFragment.TAG)
                R.id.menu_record -> showTopLevelFragment(RecordTripFragment(), RecordTripFragment.TAG)
                R.id.menu_history -> showTopLevelFragment(HistoryFragment(), HistoryFragment.TAG)
                R.id.menu_insights -> showTopLevelFragment(InsightsFragment(), InsightsFragment.TAG)
                R.id.menu_profile -> showTopLevelFragment(ProfileFragment(), ProfileFragment.TAG)
            }
            true
        }

        supportFragmentManager.addOnBackStackChangedListener {
            updateBottomNavigationVisibility()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                (application as DriveTrackerApplication).container.authRepository.session.collect { session ->
                    if (session == null) {
                        lastRemoteRefreshUserId = null
                        clearBackStack()
                        replaceRootFragment(AuthFragment(), AuthFragment.TAG)
                        binding.bottomNavigation.visibility = View.GONE
                    } else {
                        if (session.userId != lastRemoteRefreshUserId) {
                            lastRemoteRefreshUserId = session.userId
                            launch {
                                (application as DriveTrackerApplication).container.tripRepository.refreshTripsFromRemote()
                            }
                        }

                        val current = supportFragmentManager.findFragmentById(R.id.fragment_container)
                        if (current == null || current is AuthFragment || current is CreateAccountFragment) {
                            binding.bottomNavigation.selectedItemId = R.id.menu_dashboard
                            replaceRootFragment(DashboardFragment(), DashboardFragment.TAG)
                            binding.bottomNavigation.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    fun openTripDetail(tripId: Long) {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_container,
                TripDetailFragment.newInstance(tripId),
                TripDetailFragment.TAG,
            )
            .addToBackStack(TripDetailFragment.TAG)
            .commit()
        binding.bottomNavigation.visibility = View.GONE
    }

    fun openCreateAccount() {
        val current = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (current is CreateAccountFragment) return

        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_container,
                CreateAccountFragment(),
                CreateAccountFragment.TAG,
            )
            .addToBackStack(CreateAccountFragment.TAG)
            .commit()
        binding.bottomNavigation.visibility = View.GONE
    }

    private fun showTopLevelFragment(fragment: Fragment, tag: String) {
        clearBackStack()
        replaceRootFragment(fragment, tag)
        binding.bottomNavigation.visibility = View.VISIBLE
    }

    private fun replaceRootFragment(fragment: Fragment, tag: String) {
        val current = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (current?.javaClass == fragment.javaClass) return

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment, tag)
            .commit()
    }

    private fun clearBackStack() {
        supportFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    private fun updateBottomNavigationVisibility() {
        val current = supportFragmentManager.findFragmentById(R.id.fragment_container)
        binding.bottomNavigation.visibility = if (
            current is TripDetailFragment ||
            current is AuthFragment ||
            current is CreateAccountFragment
        ) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }
}
