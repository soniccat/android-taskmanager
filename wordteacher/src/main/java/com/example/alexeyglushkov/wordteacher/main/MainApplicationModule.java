package com.example.alexeyglushkov.wordteacher.main;

import android.content.Context;

import com.example.alexeyglushkov.authcachemanager.AccountCacheStore;
import com.example.alexeyglushkov.authorization.Auth.AccountStore;
import com.example.alexeyglushkov.authorization.OAuth.OAuthWebClient;
import com.example.alexeyglushkov.cachemanager.Storage;
import com.example.alexeyglushkov.cachemanager.disk.DiskStorage;
import com.example.alexeyglushkov.taskmanager.task.SimpleTaskManager;
import com.example.alexeyglushkov.taskmanager.task.TaskManager;
import com.example.alexeyglushkov.wordteacher.authorization.AuthActivityProxy;

import java.io.File;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module(includes = ContextModule.class)
public class MainApplicationModule {
    private Storage storage;

    @Provides
    @MainScope
    Storage storage(@Named("appContext") Context context) {
        if (storage == null) {
            File cacheDir = context.getDir("ServiceCache", Context.MODE_PRIVATE);
            storage = new DiskStorage(cacheDir);
        }
        return storage;
    }

    @Provides
    @MainScope
    TaskManager taskManager() {
        return new SimpleTaskManager(10);
    }

    @Provides
    @MainScope
    OAuthWebClient webClient() {
        return new AuthActivityProxy();
    }

    @Provides
    @MainScope
    AccountStore accountStore(@Named("appContext") Context context) {
        File authDir = context.getDir("AuthFolder", Context.MODE_PRIVATE);
        return new AccountCacheStore(authDir);
    }
}
