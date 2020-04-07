package com.aglushkov.wordteacher.general.networkstatus

import android.content.Context
import com.aglushkov.wordteacher.general.resource.CustomStateFlow
import javax.inject.Inject

class ConnectivityManager constructor(val context: Context) {
    private val stateFlow = CustomStateFlow<Boolean>(false)
    val flow = stateFlow.flow

    var isDeviceOnline = false
        private set
    var isWifiMode = false
        private set

    fun updateConnectivityState() {
        updateOnlineStateFlags()
        if (stateFlow.value != isDeviceOnline) {
            stateFlow.offer(isDeviceOnline)
        }
    }

    private fun updateOnlineStateFlags() {
        isDeviceOnline = false
        isWifiMode = false

        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val networkInfo = manager.activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnected) {
            isDeviceOnline = true
            val isWifi = networkInfo.type == android.net.ConnectivityManager.TYPE_WIFI
            val isWiMax = networkInfo.type == android.net.ConnectivityManager.TYPE_WIMAX
            isWifiMode = isWifi || isWiMax
        }
    }

    fun is3gMode(): Boolean {
        return isDeviceOnline && !isWifiMode
    }
}