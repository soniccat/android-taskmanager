package com.example.alexeyglushkov.cachemanager.clients;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.alexeyglushkov.cachemanager.StorageMetadata;
import com.example.alexeyglushkov.cachemanager.Storage;
import com.example.alexeyglushkov.tools.TimeTools;

/**
 * Created by alexeyglushkov on 03.03.18.
 */

public class StorageClient implements IStorageClient {
    private CacheMode cacheMode = CacheMode.CHECK_CACHE_IF_ERROR_THEN_LOAD;
    private @NonNull Storage cache;
    private boolean deleteIfExpired = true;
    private long defaultDuration;

    public StorageClient(@NonNull Storage cache) {
        this(cache, 0);
    }

    public StorageClient(@NonNull Storage cache, long duration) {
        this.cache = cache;
        this.defaultDuration = duration;
    }

    //// Actions

    public void putValue(String key, Object value) throws Exception {
        putValue(key, value, defaultDuration);
    }

    public void putValue(String key, Object value, long duration) throws Exception {
        StorageMetadata metadata = cache.createMetadata();

        if (duration != 0) {
            long expireTime = TimeTools.currentTimeSeconds() + duration;
            metadata.setExpireTime(expireTime);
        }

        cache.put(key, value, metadata);
    }

    public @Nullable Object getCachedValue(String cacheKey) throws Exception {
        StorageMetadata metadata = cache.getMetadata(cacheKey);

        Object result = null;
        if (metadata != null) {
            if (deleteIfExpired && metadata.isExpired()) {
                cache.remove(cacheKey);

            } else {
                result = cache.getValue(cacheKey);
            }
        }
        return result;
    }

    //// Setters / Getters

    // Setters

    public void setCacheMode(CacheMode cacheMode) {
        this.cacheMode = cacheMode;
    }

    public void setDeleteIfExpired(boolean deleteIfExpired) {
        this.deleteIfExpired = deleteIfExpired;
    }

    public void setDefaultDuration(long defaultDuration) {
        this.defaultDuration = defaultDuration;
    }

    // Getters

    public CacheMode getCacheMode() {
        return cacheMode;
    }

    @NonNull
    public Storage getCache() {
        return cache;
    }
}
