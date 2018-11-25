package com.example.alexeyglushkov.cachemanager.clients;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by alexeyglushkov on 03.03.18.
 */

public interface Cache {
    enum CacheMode {
        CHECK_CACHE_IF_ERROR_THEN_LOAD,
        IGNORE_CACHE,
        ONLY_LOAD_FROM_CACHE,
        ONLY_STORE_TO_CACHE
    }

    void putValue(@NonNull String key, @NonNull Object value) throws Exception;
    @Nullable <T> T getCachedValue(@NonNull String cacheKey) throws Exception;

    CacheMode getCacheMode();
    void setCacheMode(CacheMode mode);

    //// Inner classes
    class CacheEmptyError extends Error {
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
}
