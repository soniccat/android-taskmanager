package com.example.alexeyglushkov.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.alexeyglushkov.cachemanager.StorageMetadata;
import com.example.alexeyglushkov.cachemanager.StorageProvider;
import com.example.alexeyglushkov.taskmanager.loader.http.HTTPConnectionBytesReader;
import com.example.alexeyglushkov.taskmanager.loader.http.HttpBytesTransport;
import com.example.alexeyglushkov.taskmanager.loader.http.HttpURLConnectionProvider;

import java.io.ByteArrayInputStream;

/**
 * Created by alexeyglushkov on 26.11.15.
 */
// TODO: Think about an universal cache task, could be a decorator probably
// TODO: try to create a TaskTransportDecorator
    // or a decorator for TaskProvider
public class HttpCacheableTransport extends HttpBytesTransport {
    private static final String TAG = "CHLT";

    public enum CacheMode {
        CHECK_CACHE_IF_ERROR_THEN_LOAD,
        IGNORE_CACHE,
        ONLY_LOAD_FROM_CACHE,
        ONLY_STORE_TO_CACHE
    }

    @Nullable private StorageProvider cache;
    private boolean needStore = false;
    private boolean deleteIfExpired = true;
    private CacheMode cacheMode = CacheMode.CHECK_CACHE_IF_ERROR_THEN_LOAD;

    public HttpCacheableTransport(HttpURLConnectionProvider provider, HTTPConnectionBytesReader handler) {
        super(provider, handler);
    }

    public HttpCacheableTransport(HttpURLConnectionProvider provider, HTTPConnectionBytesReader handler, StorageProvider cache) {
        super(provider, handler);
        this.cache = cache;
    }

    public void setCache(@NonNull StorageProvider cache) {
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
    public void start() {
        boolean isCacheLoaded = true;
        if (cache != null && canLoadFromCache()) {
            try {
                isCacheLoaded = handleCacheContent();
            } catch (Exception ex) {
                Log.e(TAG, "handleCacheContent exception");
                ex.printStackTrace();
            }
        }

        if (!isCacheLoaded) {
            needStore = cacheMode != CacheMode.IGNORE_CACHE;
            super.start();
        }
    }

    private boolean handleCacheContent() throws Exception {
        boolean isLoaded = false;

        if (cache != null && applyCacheContent(cache)) {
            isLoaded = true;

        } else if (cacheMode == CacheMode.ONLY_LOAD_FROM_CACHE) {
            setError(new CacheEmptyError(getCacheKey(), null));
        }

        return isLoaded;
    }

    private boolean applyCacheContent(@NonNull StorageProvider cache) throws Exception {
        boolean isApplied = false;
        byte[] bytes = getCachedBytes(cache);

        if (bytes != null) {
            Object result = byteArrayReader.read(new ByteArrayInputStream(bytes));

            //TODO: think to call base menthod which wrap calling this two
            // now in base class something could be inserted after setHandleData and won't be called here
            setData(result);
            isApplied = true;
        }

        return isApplied;
    }

    private boolean canLoadFromCache() {
        return cacheMode != CacheMode.IGNORE_CACHE &&
                cacheMode != CacheMode.ONLY_STORE_TO_CACHE;
    }

    private byte[] getCachedBytes(@NonNull StorageProvider cache) throws Exception {
        String cacheKey = getCacheKey();
        StorageMetadata metadata = cache.getMetadata(cacheKey);

        byte[] bytes = null;
        if (metadata != null) {
            if (deleteIfExpired && metadata.isExpired()) {
                cache.remove(cacheKey);

            } else {
                bytes = (byte[]) cache.getValue(cacheKey);
            }
        }
        return bytes;
    }

    @Override
    public void setData(Object handledData) {
        super.setData(handledData);

        if (needStore && cache != null) {
            StorageMetadata metadata = cache.createMetadata();

            if (cacheStoreDuration() != 0) {
                // TODO: create a separate tools lib
                long expireTime = System.currentTimeMillis() / 1000L + cacheStoreDuration();
                metadata.setExpireTime(expireTime);
            }

            try {
                cache.put(getCacheKey(), byteArrayReader.getByteArray(), metadata);

            } catch (Exception e) {
                Log.e(TAG, "cache.put exception");
                e.printStackTrace();
            }
        }
    }

//    // TODO: looks strange
//    @Override
//    public void setError(Error error) {
//        if (getData() == null) {
//            super.setError(error);
//        }
//    }

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
