package com.example.alexeyglushkov.cachemanager.clients;

import androidx.annotation.Nullable;

/**
 * Created by alexeyglushkov on 03.03.18.
 */

public class CacheStatus {

    public static boolean canLoadFromCache(@Nullable Cache cache) {
        if (cache == null) {
            return false;
        }

        Cache.CacheMode cacheMode = cache.getCacheMode();
        return cacheMode != Cache.CacheMode.IGNORE_CACHE &&
                cacheMode != Cache.CacheMode.ONLY_STORE_TO_CACHE;
    }

    public static boolean canWriteToCache(@Nullable Cache client) {
        if (client == null) {
            return false;
        }

        Cache.CacheMode cacheMode = client.getCacheMode();
        return cacheMode != Cache.CacheMode.IGNORE_CACHE &&
                cacheMode != Cache.CacheMode.ONLY_LOAD_FROM_CACHE;
    }
}
