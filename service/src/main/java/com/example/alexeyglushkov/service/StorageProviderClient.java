package com.example.alexeyglushkov.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.alexeyglushkov.cachemanager.StorageMetadata;
import com.example.alexeyglushkov.cachemanager.StorageProvider;
import com.example.alexeyglushkov.tools.TimeTools;

/**
 * Created by alexeyglushkov on 03.03.18.
 */

public class StorageProviderClient {
    public enum CacheMode {
        CHECK_CACHE_IF_ERROR_THEN_LOAD,
        IGNORE_CACHE,
        ONLY_LOAD_FROM_CACHE,
        ONLY_STORE_TO_CACHE
    }

    private CacheMode cacheMode = CacheMode.CHECK_CACHE_IF_ERROR_THEN_LOAD;
    private @NonNull StorageProvider cache;
    private boolean deleteIfExpired = true;
    private long defaultDuration;

    public StorageProviderClient(@NonNull StorageProvider cache) {
        this.cache = cache;
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

    //// Inner classes

    public static class CacheEmptyError extends Error {
        private static final long serialVersionUID = -783104001989492147L;

        private String cacheKey;

        public CacheEmptyError(String cacheKey, Throwable throwable) {
            super("Cache is empty", throwable);
            this.cacheKey = cacheKey;
        }

        public String getCacheKey() {
            return cacheKey;
        }
    }

    //// Setters / Getters

    // Setters

    public void setCacheMode(CacheMode cacheMode) {
        this.cacheMode = cacheMode;
    }

    public void setDefaultDuration(long defaultDuration) {
        this.defaultDuration = defaultDuration;
    }

    // Getters

    public CacheMode getCacheMode() {
        return cacheMode;
    }

    public boolean canLoadFromCache() {
        return cacheMode != CacheMode.IGNORE_CACHE &&
                cacheMode != CacheMode.ONLY_STORE_TO_CACHE;
    }

    public boolean canWriteToCache() {
        return cacheMode != CacheMode.IGNORE_CACHE &&
                cacheMode != CacheMode.ONLY_LOAD_FROM_CACHE;
    }

    @NonNull
    public StorageProvider getCache() {
        return cache;
    }
}
