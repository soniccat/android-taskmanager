package com.example.alexeyglushkov.service;

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

    private String getCacheKey() {
        return provider.getURL().toString();
    }

    @Override
    public void startTask() {
        if (cache != null) {
            byte[] bytes = (byte[])cache.getValue(getCacheKey());

            if (bytes != null) {
                Object result = byteArrayReader.getByteArrayHandler().handleByteArrayBuffer(bytes);
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
            cache.put(getCacheKey(), byteArrayReader.getByteArray(), null);
        }
    }

    /*
    @Override
    protected Object handleStream(InputStream stream) {
        stream.mark(0);
        int chunkSize = 1024;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] bytes = new byte[chunkSize];
        int n = 0;
        try {
            while ((n = stream.read(bytes)) != -1) {
                outputStream.write(bytes);
            }

            stream.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (cache != null) {
            cache.put(getCacheKey(), outputStream.toByteArray(), null);
        }

        return super.handleStream(stream);
    }*/
}
