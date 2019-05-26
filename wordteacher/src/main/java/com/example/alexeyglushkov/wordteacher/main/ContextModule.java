package com.example.alexeyglushkov.wordteacher.main;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ContextModule {
    private Context context;

    ContextModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    Context getContext() {
        return context;
    }
}
