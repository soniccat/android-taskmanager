package com.example.alexeyglushkov.service;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import com.example.alexeyglushkov.cachemanager.clients.IStorageClient;
import com.example.alexeyglushkov.cachemanager.clients.StorageClients;
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.InputStreamDataReaders;
import com.example.alexeyglushkov.taskmanager.loader.http.HTTPConnectionBytesReader;
import com.example.alexeyglushkov.taskmanager.loader.http.HttpBytesTransport;
import com.example.alexeyglushkov.taskmanager.loader.http.HttpURLConnectionProvider;

import java.io.ByteArrayInputStream;

/**
 * Created by alexeyglushkov on 26.11.15.
 */
public class HttpCacheableTransport<T> extends HttpBytesTransport<T> {
    private static final String TAG = "CHLT";

    private boolean needStore = false;
    private @Nullable IStorageClient cacheClient;

    public HttpCacheableTransport(HttpURLConnectionProvider provider, HTTPConnectionBytesReader<T> handler) {
        this(provider, handler, null);
    }

    public HttpCacheableTransport(HttpURLConnectionProvider provider, HTTPConnectionBytesReader<T> handler, @Nullable IStorageClient cacheClient) {
        super(provider, handler);
        this.cacheClient = cacheClient;
    }

    public void setCacheClient(@NonNull IStorageClient cacheClient) {
        this.cacheClient = cacheClient;
    }

    private String getCacheKey() {
        return provider.getURL().toString();
    }

    @Override
    public void start() {
        boolean isCacheLoaded = false;
        if (cacheClient != null && StorageClients.canLoadFromCache(cacheClient)) {
            try {
                isCacheLoaded = handleCacheContent();
            } catch (Exception ex) {
                Log.e(TAG, "handleCacheContent exception");
                ex.printStackTrace();
            }
        }

        boolean canStartLoading = cacheClient == null || cacheClient.getCacheMode() != IStorageClient.CacheMode.ONLY_LOAD_FROM_CACHE;
        if (canStartLoading && !isCacheLoaded) {
            needStore = cacheClient != null && StorageClients.canWriteToCache(cacheClient);
            super.start();
        }
    }

    private boolean handleCacheContent() throws Exception {
        boolean isLoaded = false;

        if (cacheClient != null && applyCacheContent(cacheClient)) {
            isLoaded = true;

        } else if (cacheClient != null && cacheClient.getCacheMode() == IStorageClient.CacheMode.ONLY_LOAD_FROM_CACHE) {
            setError(new IStorageClient.CacheEmptyError(getCacheKey(), null));
        }

        return isLoaded;
    }

    private boolean applyCacheContent(@NonNull IStorageClient client) throws Exception {
        boolean isApplied = false;
        byte[] bytes = getCachedBytes(client);

        if (bytes != null) {
            Object result = InputStreamDataReaders.readOnce(byteArrayReader, new ByteArrayInputStream(bytes));

            setData(result);
            isApplied = true;
        }

        return isApplied;
    }

    private @Nullable byte[] getCachedBytes(@NonNull IStorageClient client) throws Exception {
        return (byte[])client.getCachedValue(getCacheKey());
    }

    @Override
    public void setData(Object handledData) {
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
