package com.example.alexeyglushkov.cachemanager.clients;

import androidx.annotation.Nullable;

/**
 * Created by alexeyglushkov on 03.03.18.
 */

public class StorageClients {

    public static boolean canLoadFromCache(@Nullable StorageClient client) {
        if (client == null) {
            return false;
        }

        StorageClient.CacheMode cacheMode = client.getCacheMode();
        return cacheMode != StorageClient.CacheMode.IGNORE_CACHE &&
                cacheMode != StorageClient.CacheMode.ONLY_STORE_TO_CACHE;
    }

    public static boolean canWriteToCache(@Nullable StorageClient client) {
        if (client == null) {
            return false;
        }

        StorageClient.CacheMode cacheMode = client.getCacheMode();
        return cacheMode != StorageClient.CacheMode.IGNORE_CACHE &&
                cacheMode != StorageClient.CacheMode.ONLY_LOAD_FROM_CACHE;
    }
}
