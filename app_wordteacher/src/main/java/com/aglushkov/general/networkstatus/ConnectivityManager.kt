package com.aglushkov.general.networkstatus

import android.content.Context
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.NetworkRequest
import android.os.Build
import com.aglushkov.general.resource.CustomStateFlow

class ConnectivityManager constructor(val context: Context) {
    private var connectivityManager = getConnectivityManager()

    private val stateFlow = CustomStateFlow<Boolean>(false)
    val flow = stateFlow.flow

    @Volatile
    var isDeviceOnline = false
        private set

    @Volatile
    var isWifiMode = false
        private set

    private var networkCallback = object : android.net.ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            checkNetworkState()
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            checkNetworkState()
        }
    }

    fun register() {
        registerNetworkCallback()
    }

    fun unregister() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun registerNetworkCallback() {
        val builder = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        connectivityManager.registerNetworkCallback(builder.build(), networkCallback)
    }

    fun checkNetworkState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            updateCapabilities(network)
        } else {
            updateCapabilities(connectivityManager.activeNetworkInfo)
        }

        if (stateFlow.value != isDeviceOnline) {
            stateFlow.offer(isDeviceOnline)
        }
    }

    private fun updateCapabilities(network: Network?) {
        val capabilities = if (network != null) connectivityManager.getNetworkCapabilities(network) else null
        capabilities?.let {
            isDeviceOnline = true
            isWifiMode = it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        } ?: run {
            isDeviceOnline = false
            isWifiMode = false
        }
    }

    private fun updateCapabilities(networkInfo: NetworkInfo?) {
        if (networkInfo?.isConnected == true) {
            isDeviceOnline = true
            val isWifi = networkInfo.type == android.net.ConnectivityManager.TYPE_WIFI
            val isWiMax = networkInfo.type == android.net.ConnectivityManager.TYPE_WIMAX
            isWifiMode = isWifi || isWiMax
        } else {
            isDeviceOnline = false
            isWifiMode = false
        }
    }

    private fun getConnectivityManager() =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
}