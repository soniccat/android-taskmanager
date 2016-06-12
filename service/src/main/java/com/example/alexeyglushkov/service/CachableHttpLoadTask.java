package com.example.alexeyglushkov.service;

import com.example.alexeyglushkov.authorization.service.Service;
import com.example.alexeyglushkov.cachemanager.CacheMetadata;
import com.example.alexeyglushkov.cachemanager.CacheProvider;
import com.example.alexeyglushkov.taskmanager.loader.http.HTTPConnectionBytesReader;
import com.example.alexeyglushkov.taskmanager.loader.http.HttpBytesLoadTask;
import com.example.alexeyglushkov.taskmanager.loader.http.HttpURLConnectionProvider;

/**
 * Created by alexeyglushkov on 26.11.15.
 */
public class CachableHttpLoadTask extends HttpBytesLoadTask {
    public enum CacheMode {
        CHECK_CACHE_IF_ERROR_THEN_LOAD,
        LOAD_IF_ERROR_THEN_CHECK_CACHE,
        IGNORE_CACHE,
        ONLY_LOAD_FROM_CACHE,
        ONLY_STORE_TO_CACHE
    }

    protected CacheProvider cache;
    private boolean needStore = false;
    private boolean deleteIfExpired = true;
    private CacheMode cacheMode = CacheMode.CHECK_CACHE_IF_ERROR_THEN_LOAD;

    public CachableHttpLoadTask(HttpURLConnectionProvider provider, HTTPConnectionBytesReader handler) {
        super(provider, handler);
    }

    public CachableHttpLoadTask(HttpURLConnectionProvider provider, HTTPConnectionBytesReader handler, CacheProvider cache) {
        super(provider, handler);
        this.cache = cache;
    }

    public void setCache(CacheProvider cache) {
        this.cache = cache;
    }

    public void setCacheMode(CacheMode cacheMode) {
        this.cacheMode = cacheMode;
    }

    public void setDeleteIfExpired(boolean deleteIfExpired) {
        this.deleteIfExpired = deleteIfExpired;
    }

    protected long cacheStoreDuration() {
        return 0;
    }

    private String getCacheKey() {
        return provider.getURL().toString();
    }

    @Override
    public void startTask() {
        boolean canLoadTask = true;
        if (cache != null && canLoadFromCache()) {
            canLoadTask = handleCacheContent();
        }

        if (canLoadTask) {
            needStore = cacheMode != CacheMode.IGNORE_CACHE;
            super.startTask();
        } else {
            getPrivate().handleTaskCompletion();
        }
    }

    private boolean handleCacheContent() {
        boolean canLoadTaskAfter = true;

        if (applyCacheContent()) {
            canLoadTaskAfter = false;

        } else if (cacheMode == CacheMode.ONLY_LOAD_FROM_CACHE) {
            setError(new CacheEmptyError(getCacheKey(), null));
            canLoadTaskAfter = false;
        }

        return canLoadTaskAfter;
    }

    private boolean applyCacheContent() {
        boolean isApplied = false;
        byte[] bytes = getCachedBytes();

        if (bytes != null) {
            Object result = bytes;
            if (byteArrayReader.getByteArrayHandler() != null) {
                result = byteArrayReader.getByteArrayHandler().handleByteArrayBuffer(bytes);
            }

            //TODO: think to call base menthod which wrap calling this two
            // now in base class something could be inserted after setHandleData and won't be called here
            setHandledData(result);
            isApplied = true;
        }

        return isApplied;
    }

    private boolean canLoadFromCache() {
        return cacheMode != CacheMode.IGNORE_CACHE &&
                cacheMode != CacheMode.LOAD_IF_ERROR_THEN_CHECK_CACHE &&
                cacheMode != CacheMode.ONLY_STORE_TO_CACHE;
    }

    private byte[] getCachedBytes() {
        String cacheKey = getCacheKey();
        CacheMetadata metadata = cache.getMetadata(cacheKey);

        byte[] bytes = null;
        if (metadata != null) {
            long currentTime = System.currentTimeMillis() / 1000L;
            if (currentTime < metadata.getExpireTime()) {
                bytes = (byte[]) cache.getValue(cacheKey);

            } else if (deleteIfExpired) {
                cache.remove(cacheKey);
            }
        }
        return bytes;
    }

    @Override
    public void setHandledData(Object handledData) {
        super.setHandledData(handledData);

        if (needStore && cache != null) {
            CacheMetadata metadata = cache.createMetadata();

            if (cacheStoreDuration() != 0) {
                // TODO: create a separate tools lib
                long expireTime = System.currentTimeMillis() / 1000L + cacheStoreDuration();
                metadata.setExpireTime(expireTime);
            }
            cache.put(getCacheKey(), byteArrayReader.getByteArray(), metadata);
        }
    }

    @Override
    public void setError(Error error) {
        if (cacheMode == CacheMode.LOAD_IF_ERROR_THEN_CHECK_CACHE) {
            needStore = false;
            applyCacheContent();
        }

        if (getHandledData() == null) {
            super.setError(error);
        }
    }

    public class CacheEmptyError extends Error {
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
