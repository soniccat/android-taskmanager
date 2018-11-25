package com.example.alexeyglushkov.service;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import com.example.alexeyglushkov.cachemanager.clients.Cache;
import com.example.alexeyglushkov.cachemanager.clients.CacheStatus;
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.InputStreamDataReaders;
import com.example.alexeyglushkov.taskmanager.loader.http.HTTPConnectionBytesReader;
import com.example.alexeyglushkov.taskmanager.loader.http.HttpBytesTransport;
import com.example.alexeyglushkov.taskmanager.loader.http.HttpURLConnectionProvider;

import java.io.ByteArrayInputStream;

/**
 * Created by alexeyglushkov on 26.11.15.
 */
public class HttpCacheableTransport<T> extends HttpBytesTransport<T> {
    private static final String TAG = "HTTPCacheableTr";

    private boolean needStore = false;
    private @Nullable
    Cache cacheClient;

    public HttpCacheableTransport(HttpURLConnectionProvider provider, HTTPConnectionBytesReader<T> handler) {
        this(provider, handler, null);
    }

    public HttpCacheableTransport(HttpURLConnectionProvider provider, HTTPConnectionBytesReader<T> handler, @Nullable Cache cacheClient) {
        super(provider, handler);
        this.cacheClient = cacheClient;
    }

    public void setCacheClient(@NonNull Cache cacheClient) {
        this.cacheClient = cacheClient;
    }

    private String getCacheKey() {
        return provider.getURL().toString();
    }

    @Override
    public void start() {
        boolean isCacheLoaded = false;
        if (CacheStatus.canLoadFromCache(cacheClient)) {
            try {
                isCacheLoaded = handleCacheContent();
            } catch (Exception ex) {
                Log.e(TAG, "handleCacheContent exception");
                ex.printStackTrace();
            }
        }

        boolean canStartLoading = cacheClient == null || cacheClient.getCacheMode() != Cache.CacheMode.ONLY_LOAD_FROM_CACHE;
        if (canStartLoading && !isCacheLoaded) {
            needStore = CacheStatus.canWriteToCache(cacheClient);
            super.start();
        }
    }

    private boolean handleCacheContent() throws Exception {
        boolean isLoaded = false;

        if (cacheClient != null && applyCacheContent(cacheClient)) {
            isLoaded = true;

        } else if (cacheClient != null && cacheClient.getCacheMode() == Cache.CacheMode.ONLY_LOAD_FROM_CACHE) {
            setError(new Cache.CacheEmptyError(getCacheKey(), null));
        }

        return isLoaded;
    }

    private boolean applyCacheContent(@NonNull Cache client) throws Exception {
        boolean isApplied = false;
        byte[] bytes = getCachedBytes(client);

        if (bytes != null) {
            T result = InputStreamDataReaders.readOnce(byteArrayReader, new ByteArrayInputStream(bytes));

            setData(result);
            isApplied = true;
        }

        return isApplied;
    }

    private @Nullable byte[] getCachedBytes(@NonNull Cache client) throws Exception {
        return (byte[])client.getCachedValue(getCacheKey());
    }

    @Override
    public void setData(T handledData) {
        super.setData(handledData);

        if (needStore && cacheClient != null) {
            try {
                cacheClient.putValue(getCacheKey(), byteArrayReader.getByteArray());

            } catch (Exception e) {
                Log.e(TAG, "cache.put exception");
                e.printStackTrace();
            }
        }
    }
}
