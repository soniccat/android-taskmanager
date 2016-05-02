package com.example.alexeyglushkov.service;

import com.example.alexeyglushkov.cachemanager.CacheMetadata;
import com.example.alexeyglushkov.cachemanager.CacheProvider;
import com.example.alexeyglushkov.taskmanager.loader.http.HTTPConnectionBytesReader;
import com.example.alexeyglushkov.taskmanager.loader.http.HTTPConnectionStreamReader;
import com.example.alexeyglushkov.taskmanager.loader.http.HttpBytesLoadTask;
import com.example.alexeyglushkov.taskmanager.loader.http.HttpLoadTask;
import com.example.alexeyglushkov.taskmanager.loader.http.HttpURLConnectionProvider;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

/**
 * Created by alexeyglushkov on 26.11.15.
 */
public class CachedHttpLoadTask extends HttpBytesLoadTask {
    protected CacheProvider cache;
    private boolean needStore = false;

    public CachedHttpLoadTask(HttpURLConnectionProvider provider, HTTPConnectionBytesReader handler) {
        super(provider, handler);
    }

    public CachedHttpLoadTask(HttpURLConnectionProvider provider, HTTPConnectionBytesReader handler, CacheProvider cache) {
        super(provider, handler);
        this.cache = cache;
    }

    protected long cacheStoreDuration() {
        return 0;
    }

    private String getCacheKey() {
        return provider.getURL().toString();
    }

    @Override
    public void startTask() {
        if (cache != null) {
            byte[] bytes = (byte[])cache.getValue(getCacheKey());
            if (bytes != null) {
                Object result = bytes;
                if (byteArrayReader.getByteArrayHandler() != null) {
                    result = byteArrayReader.getByteArrayHandler().handleByteArrayBuffer(bytes);
                }

                //TODO: think to call base menthod which wrap calling this two
                // now in base class something could be inserted after setHandleData and won't be called here
                setHandledData(result);
                getPrivate().handleTaskCompletion();
                return;
            }
        }

        needStore = true;
        super.startTask();
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
}
