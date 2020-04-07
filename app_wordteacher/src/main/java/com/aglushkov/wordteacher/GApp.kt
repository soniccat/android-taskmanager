package com.aglushkov.wordteacher

import android.app.Application
import android.content.IntentFilter
import com.aglushkov.wordteacher.di.AppComponent
import com.aglushkov.wordteacher.di.AppComponentOwner
import com.aglushkov.wordteacher.di.DaggerAppComponent
import com.aglushkov.wordteacher.di.GeneralModule
import java.lang.Exception


class GApp: Application(), AppComponentOwner, ActivityVisibilityResolver.Listener {
    private lateinit var activityVisibilityResolver: ActivityVisibilityResolver
    override lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder().generalModule(GeneralModule(this)).build()
        appComponent.getConnectivityManager().updateConnectivityState()

        initActivityVisibilityResolver()
    }

    private fun initActivityVisibilityResolver() {
        activityVisibilityResolver = ActivityVisibilityResolver(this)
        activityVisibilityResolver.listener = this
        activityVisibilityResolver.attach()
    }

    override fun onFirstActivityStarted() {
        registerConnectivityReceiver()
    }

    override fun onLastActivityStopped() {
        unregisterConnectivetyReceiver()
    }

    private fun registerConnectivityReceiver() {
        registerReceiver(appComponent.getNetworkReceiver(), IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"))
    }

    private fun unregisterConnectivetyReceiver() {
        try {
            unregisterReceiver(appComponent.getNetworkReceiver())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}