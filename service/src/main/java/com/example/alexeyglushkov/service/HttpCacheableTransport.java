package com.example.alexeyglushkov.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.alexeyglushkov.streamlib.data_readers_and_writers.InputStreamDataReaders;
import com.example.alexeyglushkov.taskmanager.loader.http.HTTPConnectionBytesReader;
import com.example.alexeyglushkov.taskmanager.loader.http.HttpBytesTransport;
import com.example.alexeyglushkov.taskmanager.loader.http.HttpURLConnectionProvider;

import java.io.ByteArrayInputStream;

/**
 * Created by alexeyglushkov on 26.11.15.
 */
public class HttpCacheableTransport extends HttpBytesTransport {
    private static final String TAG = "CHLT";

    private boolean needStore = false;
    private @Nullable StorageProviderClient cacheClient;

    public HttpCacheableTransport(HttpURLConnectionProvider provider, HTTPConnectionBytesReader handler) {
        this(provider, handler, null);
    }

    public HttpCacheableTransport(HttpURLConnectionProvider provider, HTTPConnectionBytesReader handler, StorageProviderClient cacheClient) {
        super(provider, handler);
        this.cacheClient = cacheClient;
    }

    public void setCacheClient(@NonNull StorageProviderClient cacheClient) {
        this.cacheClient = cacheClient;
    }

    protected long cacheStoreDuration() {
        return 0;
    }

    private String getCacheKey() {
        return provider.getURL().toString();
    }

    @Override
    public void start() {
        boolean isCacheLoaded = false;
        if (cacheClient != null && cacheClient.canLoadFromCache()) {
            try {
                isCacheLoaded = handleCacheContent();
            } catch (Exception ex) {
                Log.e(TAG, "handleCacheContent exception");
                ex.printStackTrace();
            }
        }

        if (!isCacheLoaded) {
            needStore = cacheClient != null && cacheClient.canWriteToCache();
            super.start();
        }
    }

    private boolean handleCacheContent() throws Exception {
        boolean isLoaded = false;

        if (cacheClient != null && applyCacheContent(cacheClient)) {
            isLoaded = true;

        } else if (cacheClient != null && cacheClient.getCacheMode() == StorageProviderClient.CacheMode.ONLY_LOAD_FROM_CACHE) {
            setError(new StorageProviderClient.CacheEmptyError(getCacheKey(), null));
        }

        return isLoaded;
    }

    private boolean applyCacheContent(@NonNull StorageProviderClient client) throws Exception {
        boolean isApplied = false;
        byte[] bytes = getCachedBytes(client);

        if (bytes != null) {
            Object result = InputStreamDataReaders.readOnce(byteArrayReader, new ByteArrayInputStream(bytes));

            setData(result);
            isApplied = true;
        }

        return isApplied;
    }

    private @Nullable byte[] getCachedBytes(@NonNull StorageProviderClient client) throws Exception {
        return (byte[])client.getCachedValue(getCacheKey());
    }

    @Override
    public void setData(Object handledData) {
        super.setData(handledData);

        if (needStore && cacheClient != null) {
            try {
                cacheClient.putValue(getCacheKey(), byteArrayReader.getByteArray(), cacheStoreDuration());

            } catch (Exception e) {
                Log.e(TAG, "cache.put exception");
                e.printStackTrace();
            }
        }
    }
}
