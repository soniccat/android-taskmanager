package com.example.alexeyglushkov.service;

import com.example.alexeyglushkov.cachemanager.CacheProvider;
import com.example.alexeyglushkov.taskmanager.loader.http.HTTPConnectionResponseReader;
import com.example.alexeyglushkov.taskmanager.loader.http.HttpLoadTask;
import com.example.alexeyglushkov.taskmanager.loader.http.HttpURLConnectionProvider;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Created by alexeyglushkov on 26.11.15.
 */
public class CachedHttpLoadTask extends HttpLoadTask {
    protected CacheProvider cache;
    private boolean needStore = false;

    public CachedHttpLoadTask(HttpURLConnectionProvider provider, HTTPConnectionResponseReader handler) {
        super(provider, handler);
    }

    public CachedHttpLoadTask(HttpURLConnectionProvider provider, HTTPConnectionResponseReader handler, CacheProvider cache) {
        super(provider, handler);
        this.cache = cache;
    }

    @Override
    public void startTask() {
        if (cache != null) {
            String key = provider.getURL().toString();
            String string = (String)cache.getValue(key);

            if (string != null) {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8));
                handleStream(inputStream);
                getPrivate().handleTaskCompletion();
                return;
            }
        }

        needStore = true;
        super.startTask();
    }

    @Override
    protected Object handleStream(InputStream stream) {


        return super.handleStream(stream);
    }
}
