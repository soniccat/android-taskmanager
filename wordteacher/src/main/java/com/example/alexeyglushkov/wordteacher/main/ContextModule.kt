package com.example.alexeyglushkov.wordteacher.main

import android.content.Context

import javax.inject.Named
import javax.inject.Singleton

import dagger.Module
import dagger.Provides

@Module
class ContextModule internal constructor(private val context: Context) {

    @Provides
    @Named("context")
    internal fun context(): Context {
        return context
    }

    @Provides
    @Named("appContext")
    internal fun applicationContext(): Context {
        return context.applicationContext
    }
}
