package com.aglushkov.wordteacher.di

import android.content.Context
import com.aglushkov.general.networkstatus.ConnectivityManager
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
}