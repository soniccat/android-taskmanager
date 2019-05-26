package com.example.alexeyglushkov.wordteacher.main;

import android.content.Context;

import com.example.alexeyglushkov.cachemanager.disk.DiskStorage;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class MainApplicationModule {
    private Context context;
    private DiskStorage storage;

    MainApplicationModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    DiskStorage getStorage() {
        if (storage == null) {
            File cacheDir = context.getDir("ServiceCache", Context.MODE_PRIVATE);
            storage = new DiskStorage(cacheDir);
        }
        return storage;
    }
}
