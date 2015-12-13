package com.main;

import com.authorization.AuthActivityProxy;
import com.example.alexeyglushkov.authcachemanager.AccountCacheStore;
import com.example.alexeyglushkov.authorization.Auth.Account;
import com.example.alexeyglushkov.authorization.Auth.AccountStore;
import com.example.alexeyglushkov.authorization.OAuth.OAuthWebClient;
import com.example.alexeyglushkov.cachemanager.CacheCleaner;
import com.example.alexeyglushkov.cachemanager.CacheProvider;
import com.example.alexeyglushkov.cachemanager.DiskCacheCleaner;
import com.example.alexeyglushkov.cachemanager.DiskCacheProvider;
import com.example.alexeyglushkov.taskmanager.task.SimpleTask;
import com.example.alexeyglushkov.taskmanager.task.Task;
import com.rssclient.model.RssStorage;
import com.example.alexeyglushkov.taskmanager.task.SimpleTaskManager;
import com.example.alexeyglushkov.taskmanager.task.TaskManager;

import android.app.Application;
import android.content.Context;

import java.io.File;

public class MainApplication extends Application {
    private AccountStore accountStore;
    private OAuthWebClient authWebClient;

    private TaskManager taskManager;
    private RssStorage rssStorage;

    private CacheProvider cacheProvider;

    public static MainApplication instance;

    public MainApplication() {
        super();
        instance = this;
        authWebClient = new AuthActivityProxy();
        taskManager = new SimpleTaskManager(10);
        rssStorage = new RssStorage("RssStorage");

        File cacheDir = getDir("ServiceCache", MODE_PRIVATE);
        cacheProvider = new DiskCacheProvider(cacheDir);

        cleanCache();
        loadAccountStore();
    }

    public RssStorage getRssStorage() {
        return rssStorage;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public AccountStore getAccountStore() {
        return accountStore;
    }

    public OAuthWebClient getAuthWebClient() {
        return authWebClient;
    }

    public CacheProvider getCacheProvider() {
        return cacheProvider;
    }

    public void cleanCache() {
        final Task cleanTask = new SimpleTask() {
            @Override
            public void startTask() {
                CacheCleaner cleaner = new DiskCacheCleaner();
                cleaner.clean(getCacheProvider());

                getPrivate().handleTaskCompletion();
            }
        };

        taskManager.addTask(cleanTask);
    }

    public void loadAccountStore() {
        final Task loadAccountTask = new SimpleTask() {
            @Override
            public void startTask() {
                File authDir = getDir("AuthFolder", Context.MODE_PRIVATE);
                AccountCacheStore store = new AccountCacheStore(authDir);
                store.load();
                restoreAccounts(store);

                getPrivate().setTaskUserData(store);
                getPrivate().handleTaskCompletion();
            }
        };

        loadAccountTask.setTaskCallback(new Task.Callback() {
            @Override
            public void onCompleted(boolean cancelled) {
                MainApplication.this.accountStore = (AccountCacheStore) loadAccountTask.getTaskUserData();
            }
        });

        taskManager.addTask(loadAccountTask);
    }

    private void restoreAccounts(AccountCacheStore store) {
        for (Account acc : store.getAccounts()) {
            acc.setAuthCredentialStore(getAccountStore());
            Networks.restoreAuthorizer(acc);
        }
    }
}
