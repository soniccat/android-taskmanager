package com.example.alexeyglushkov.cachemanager.clients;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by alexeyglushkov on 03.03.18.
 */

public class StorageClients {

    public static boolean canLoadFromCache(@Nullable IStorageClient client) {
        if (client == null) {
            return false;
        }

        IStorageClient.CacheMode cacheMode = client.getCacheMode();
        return cacheMode != IStorageClient.CacheMode.IGNORE_CACHE &&
                cacheMode != IStorageClient.CacheMode.ONLY_STORE_TO_CACHE;
    }

    public static boolean canWriteToCache(@Nullable IStorageClient client) {
        if (client == null) {
            return false;
        }

        IStorageClient.CacheMode cacheMode = client.getCacheMode();
        return cacheMode != IStorageClient.CacheMode.IGNORE_CACHE &&
                cacheMode != IStorageClient.CacheMode.ONLY_LOAD_FROM_CACHE;
    }
}
