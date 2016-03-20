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
public class DiskCacheMetadata extends HashMap<String, Object>
{
    private static final long serialVersionUID = -3043981628125337011L;
    private static final String createTimeKey = "metadataCreateTime";
    private static final String expireTimeKey = "metadataExpireTime";
    private static final String fileSizeKey = "metadataFileSize";
    private static final String entryClassKey = "metadataEntryClass";

    private transient File file;
    private transient Serializer serializer = createSerializer();

    private static ObjectSerializer createSerializer() {
        return new ObjectSerializer();
    }

    public DiskCacheMetadata() {
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void calculateSize(File file) {
        put(fileSizeKey, file.length());
    }

    public long getFileSize() {
        return (long)get(fileSizeKey);
    }

    public void setFileSize(long fileSize) {
        put(fileSizeKey, fileSize);
    }

    public long getCreateTime() {
        return (long)get(createTimeKey);
    }

    public void setCreateTime(long createTime) {
        put(createTimeKey, createTime);
    }

    public long getExpireTime() {
        Long value = (Long)get(expireTimeKey);
        return value == null ? 0 : value;
    }

    public void setExpireTime(long expireTime) {
        put(expireTimeKey, expireTime);
    }

    public void setEntryClass(Class cl) {
        put(entryClassKey, cl);
    }

    public Class getEntryClass() {
        return (Class)get(entryClassKey);
    }

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

    public static DiskCacheMetadata load(File file) {
        Error error = null;
        InputStream fis = null;
        DiskCacheMetadata result = null;

        try {
            fis = new BufferedInputStream(new FileInputStream(file));
            ObjectSerializer serializer = createSerializer();
            result = (DiskCacheMetadata)serializer.read(fis);
            result.setFile(file);

        } catch (Exception ex) {
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
}