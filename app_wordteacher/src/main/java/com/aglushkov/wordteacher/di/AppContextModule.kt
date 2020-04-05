package com.aglushkov.wordteacher.di

import android.content.Context
import dagger.Module
import dagger.Provides


@Module
class AppContextModule(private val aContext: Context) {
    @Provides
    fun getContext(): Context {
        return aContext
    }
}