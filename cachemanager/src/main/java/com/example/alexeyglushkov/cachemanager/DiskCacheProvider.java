package com.example.alexeyglushkov.cachemanager;

import com.noveogroup.android.cache.disk.DiskCache;
import com.noveogroup.android.cache.disk.MetaData;
import com.noveogroup.android.cache.io.Serializer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

/**
 * Created by alexeyglushkov on 26.09.15.
 */
public class DiskCacheProvider implements CacheProvider {

    File directory;

    public DiskCacheProvider(File directory) {
        this.directory = directory;
    }

    @Override
    public Error store(String key, CacheEntry entry, CacheMetadata metadata) {
        Error error = prepareDirectory();
        if (error != null) {

        }

        return error;
    }

    private Error prepareDirectory() {
        if (!directory.exists()) {
            try {
                directory.createNewFile();
            } catch (IOException ex) {
                return new Error(ex.getMessage());
            }
        }

        return null;
    }

    private Error write(CacheEntry entry, CacheMetadata metadata) {
        Error error = null;


        return error;
    }

    @Override
    public CacheEntry getEntry(String key) {
        return null;
    }

    public CacheMetadata getMetadata(String key) {
        return null;
    }

    @Override
    public void remove(String key) {
    }
}
