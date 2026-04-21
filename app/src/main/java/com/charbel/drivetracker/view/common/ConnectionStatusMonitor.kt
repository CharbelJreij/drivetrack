package com.charbel.drivetracker.view.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun Context.observeConnectionStatus(): Flow<Boolean> = callbackFlow {
    val connectivityManager = getSystemService(ConnectivityManager::class.java)
    if (connectivityManager == null) {
        trySend(false)
        close()
        return@callbackFlow
    }

    fun currentStatus(): Boolean {
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    trySend(currentStatus())

    val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            trySend(currentStatus())
        }

        override fun onLost(network: Network) {
            trySend(currentStatus())
        }

        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            trySend(
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED),
            )
        }
    }

    connectivityManager.registerDefaultNetworkCallback(callback)
    awaitClose {
        runCatching { connectivityManager.unregisterNetworkCallback(callback) }
    }
}
