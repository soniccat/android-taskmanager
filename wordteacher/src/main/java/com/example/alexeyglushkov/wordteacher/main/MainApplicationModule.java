package com.example.alexeyglushkov.wordteacher.main;

import android.content.Context;

import com.example.alexeyglushkov.cachemanager.Storage;
import com.example.alexeyglushkov.cachemanager.disk.DiskStorage;
import com.example.alexeyglushkov.taskmanager.task.SimpleTaskManager;
import com.example.alexeyglushkov.taskmanager.task.TaskManager;

import java.io.File;

import dagger.Module;
import dagger.Provides;

@Module
public class MainApplicationModule {
    private Storage storage;

    @Provides
    @MainScope
    Storage getStorage(Context context) {
        if (storage == null) {
            File cacheDir = context.getDir("ServiceCache", Context.MODE_PRIVATE);
            storage = new DiskStorage(cacheDir);
        }
        return storage;
    }

    @Provides
    @MainScope
    TaskManager getTaskManager() {
        return new SimpleTaskManager(10);
    }
}
