package com.example.alexeyglushkov.cachemanager;

import com.example.alexeyglushkov.streamlib.Serializer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Created by alexeyglushkov on 26.09.15.
 */
public class DiskCacheProvider implements CacheProvider {

    private File directory;
    private Serializer serializer;
    private Error lastError;

    public DiskCacheProvider(File directory, Serializer serializer) {
        this.directory = directory;
        this.serializer = serializer;
    }

    @Override
    public Error store(String key, Object entry, Serializable metadata) {
        Error error = prepareDirectory();
        if (error == null) {
            error = write(key, entry, (DiskCacheMetadata)metadata);
        }

        return error;
    }

    private Error prepareDirectory() {
        if (!directory.exists()) {
            try {
                directory.createNewFile();
            } catch (IOException ex) {
                return new Error("DiskCacheProvider prepareDirectory exception:" + ex.getMessage());
            }
        }

        return null;
    }

    private File getKeyFile(String key) {
        String fileName = Integer.toString(key.hashCode());
        File file = new File(directory.getPath() + File.pathSeparator + fileName);
        return file;
    }

    private File getKeyMetadataFile(String key) {
        String fileName = Integer.toString(key.hashCode()) + "_metadata";
        File file = new File(directory.getPath() + File.pathSeparator + fileName);
        return file;
    }

    private Error write(String key, Object object, DiskCacheMetadata metadata) {
        Error error = null;
        error = writeMetadata(key, metadata);

        File file = null;
        if (error == null) {
            file = getKeyFile(key);
            if (file.exists()) {
                if (!file.delete()) {
                    error = new Error("DiskCacheProvider write delete: can't delete cache file");
                }
            }
        }

        if (error == null) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                error = new Error("DiskCacheProvider write createNewFile exception:" + ex.getMessage());
            }
        }

        if (error == null) {
            DiskCacheEntry entry = new DiskCacheEntry(file, metadata, serializer);
            error = entry.write();
        }

        return error;
    }

    private Error writeMetadata(String key, DiskCacheMetadata metadata) {

    }

    @Override
    public Object getValue(String key) {
        Object entry = null;
        File file = getKeyFile(key);

        if (file.exists()) {
            lastError = new Error("DiskCacheProvider getValue: file doesn't exist");
        }

        if (lastError == null) {
            InputStream inputStream = null;
            try {
                inputStream = new BufferedInputStream(new FileInputStream(file));
                entry = (DiskCacheEntry)serializer.read(inputStream);
            } catch (Exception ex) {
                lastError = new Error("DiskCacheProvider getValue exception: " + ex.getMessage());
            } finally {

            }
        }

        return entry;
    }

    public Serializable getMetadata(String key) {
        return null;
    }

    @Override
    public void remove(String key) {
    }

    @Override
    public Error getError() {
        return lastError;
    }
}
