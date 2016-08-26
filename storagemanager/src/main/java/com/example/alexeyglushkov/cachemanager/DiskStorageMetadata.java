package com.example.alexeyglushkov.cachemanager;

import com.example.alexeyglushkov.streamlib.serializers.ObjectSerializer;
import com.example.alexeyglushkov.streamlib.serializers.Serializer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * Created by alexeyglushkov on 04.10.15.
 */
public class DiskStorageMetadata extends HashMap<String, Object> implements StorageMetadata
{
    private static final long serialVersionUID = -3043981628125337011L;
    private static final String CREATE_TIME_KEY = "metadataCreateTime";
    private static final String EXPIRE_TIME_KEY = "metadataExpireTime";
    private static final String FILE_SIZE_KEY = "metadataFileSize";
    private static final String ENTRY_CLASS_KEY = "metadataEntryClass";

    private transient File file;
    private transient Serializer serializer = createSerializer();

    //// Initialization

    public DiskStorageMetadata() {
    }

    //// Actions

    public Error write() {
        Error error = null;
        OutputStream os = null;

        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            error = serializer.write(os, this);

        } catch (Exception ex) {
            error = new Error("DiskCacheEntry write open stream exception: " + ex.getMessage());
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (Exception ex) {
                    error = new Error("DiskCacheEntry write close stream exception: " + ex.getMessage());
                }
            }
        }

        return error;
    }

    // TODO: throws an exception
    public static DiskStorageMetadata load(File file) {
        Error error = null;
        InputStream fis = null;
        DiskStorageMetadata result = null;

        try {
            fis = new BufferedInputStream(new FileInputStream(file));
            ObjectSerializer serializer = createSerializer();
            result = (DiskStorageMetadata)serializer.read(fis);
            result.setFile(file);

        } catch (Exception ex) {
            // TODO: return these errors
            error = new Error("DiskCacheEntry exception: " + ex.getMessage());
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception ex) {
                    error = new Error("DiskCacheEntry exception: " + ex.getMessage());
                }
            }
        }

        return result;
    }

    public static Error delete(File file) {
        Error error = null;
        if (!file.delete()) {
            error = new Error("DiskCacheMetadata delete: can't delete file " + file.getAbsolutePath());
        }

        return error;
    }

    public void calculateSize(File file) {
        put(FILE_SIZE_KEY, file.length());
    }

    //// Construction Methods

    private static ObjectSerializer createSerializer() {
        return new ObjectSerializer();
    }

    //// Interfaces

    // StorageMetadata

    public long getContentSize() {
        return (long)get(FILE_SIZE_KEY);
    }

    public void setContentSize(long fileSize) {
        put(FILE_SIZE_KEY, fileSize);
    }

    public long getCreateTime() {
        return (long)get(CREATE_TIME_KEY);
    }

    public void setCreateTime(long createTime) {
        put(CREATE_TIME_KEY, createTime);
    }

    public long getExpireTime() {
        Long value = (Long)get(EXPIRE_TIME_KEY);
        return value == null ? 0 : value;
    }

    @Override
    public boolean isExpired() {
        long currentTime = System.currentTimeMillis() / 1000L;
        boolean isExpired = hasExpireTime() && currentTime >= getExpireTime();
        return isExpired;
    }

    public boolean hasExpireTime() {
        return containsKey(EXPIRE_TIME_KEY);
    }

    public void setExpireTime(long expireTime) {
        put(EXPIRE_TIME_KEY, expireTime);
    }

    public void setEntryClass(Class cl) {
        put(ENTRY_CLASS_KEY, cl);
    }

    public Class getEntryClass() {
        return (Class)get(ENTRY_CLASS_KEY);
    }

    //// Setters

    public void setFile(File file) {
        this.file = file;
    }

    //// Getters

    public File getFile() {
        return file;
    }
}
