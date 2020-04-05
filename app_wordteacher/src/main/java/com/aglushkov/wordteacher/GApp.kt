package com.aglushkov.wordteacher

import android.app.Application
import com.aglushkov.wordteacher.di.AppComponent
import com.aglushkov.wordteacher.di.AppComponentOwner
import com.aglushkov.wordteacher.di.AppContextModule
import com.aglushkov.wordteacher.di.DaggerAppComponent

class GApp: Application(), AppComponentOwner {
    override lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder().appContextModule(AppContextModule(this)).build()
    }
}