package com.aglushkov.wordteacher.di

import android.content.Context
import com.aglushkov.wordteacher.general.networkstatus.ConnectivityManager
import com.aglushkov.wordteacher.general.networkstatus.NetworkReceiver
import dagger.Module
import dagger.Provides


@Module
class GeneralModule(private val aContext: Context) {
    @Provides
    fun getContext(): Context {
        return aContext
    }

    @AppComp
    @Provides
    fun getConnectivityManager(context: Context): ConnectivityManager {
        return ConnectivityManager(context)
    }

    @AppComp
    @Provides
    fun getNetworkReceiver(): NetworkReceiver {
        return NetworkReceiver()
    }
}