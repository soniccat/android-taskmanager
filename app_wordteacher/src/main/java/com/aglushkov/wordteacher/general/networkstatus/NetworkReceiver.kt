package com.aglushkov.wordteacher.general.networkstatus

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.aglushkov.wordteacher.di.AppComp
import com.aglushkov.wordteacher.di.AppComponentOwner
import javax.inject.Inject

class NetworkReceiver constructor() : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        (context as AppComponentOwner).appComponent.getConnectivityManager().updateConnectivityState()
    }
}