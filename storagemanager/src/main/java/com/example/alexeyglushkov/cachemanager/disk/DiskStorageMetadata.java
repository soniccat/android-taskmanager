package com.example.alexeyglushkov.cachemanager.disk;

import com.example.alexeyglushkov.cachemanager.StorageMetadata;
import com.example.alexeyglushkov.cachemanager.disk.serializer.DiskMetadataSerializer;
import com.example.alexeyglushkov.streamlib.serializers.Serializer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 04.10.15.
 */
public class DiskStorageMetadata implements StorageMetadata
{
    private long contentSize;
    private long createDate;
    private long expireDate;
    private Class entryClass;

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

    public static DiskStorageMetadata load(File file) throws IOException {
        InputStream fis = null;
        DiskStorageMetadata result = null;

        try {
            fis = new BufferedInputStream(new FileInputStream(file));
            Serializer serializer = createSerializer();
            result = (DiskStorageMetadata)serializer.read(fis);
            result.setFile(file);

        } finally {
            if (fis != null) {
                fis.close();
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
        contentSize = file.length();
    }

    //// Construction Methods

    private static Serializer createSerializer() {
        return new DiskMetadataSerializer();
    }

    //// Interfaces

    // StorageMetadata

    public long getContentSize() {
        return contentSize;
    }

    public void setContentSize(long fileSize) {
        contentSize = fileSize;
    }

    public long getCreateTime() {
        return createDate;
    }

    public void setCreateTime(long createTime) {
        this.createDate = createTime;
    }

    public long getExpireTime() {
        return expireDate;
    }

    @Override
    public boolean isExpired() {
        long currentTime = System.currentTimeMillis() / 1000L;
        boolean isExpired = hasExpireTime() && currentTime >= getExpireTime();
        return isExpired;
    }

    public boolean hasExpireTime() {
        return expireDate > 0;
    }

    public void setExpireTime(long expireTime) {
        this.expireDate = expireTime;
    }

    public void setEntryClass(Class cl) {
        entryClass = cl;
    }

    public Class getEntryClass() {
        return entryClass;
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
