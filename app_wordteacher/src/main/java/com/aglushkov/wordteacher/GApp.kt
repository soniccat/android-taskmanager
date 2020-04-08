package com.aglushkov.wordteacher

import android.app.Application
import android.content.IntentFilter
import com.aglushkov.general.app.ActivityVisibilityResolver
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
        appComponent.getConnectivityManager().checkNetworkState()

        initActivityVisibilityResolver()
    }

    private fun initActivityVisibilityResolver() {
        activityVisibilityResolver = ActivityVisibilityResolver(this)
        activityVisibilityResolver.listener = this
        activityVisibilityResolver.attach()
    }

    override fun onFirstActivityStarted() {
        appComponent.getConnectivityManager().register()
    }

    override fun onLastActivityStopped() {
        appComponent.getConnectivityManager().unregister()
    }
}