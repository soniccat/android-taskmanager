package com.example.alexeyglushkov.cachemanager;

import com.example.alexeyglushkov.streamlib.ObjectSerializer;
import com.example.alexeyglushkov.streamlib.Serializer;

import org.mockito.internal.util.collections.ArrayUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by alexeyglushkov on 26.09.15.
 */
public class DiskCacheProvider implements CacheProvider {

    private File directory;
    private Map<Class,Serializer> serializerMap;
    private Error lastError;

    public DiskCacheProvider(File directory) {
        this.directory = directory;
        this.serializerMap = new HashMap<>();
    }

    public void setSerializer(Serializer serializer, Class cl) {
        serializerMap.put(cl, serializer);
    }

    @Override
    public Error put(String key, Object entry, Serializable metadata) {
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
            Serializer serializer = getSerializer(object.getClass());
            assert serializer != null;

            DiskCacheEntry entry = new DiskCacheEntry(file, object, metadata, serializer);
            error = entry.write();
        }

        if (error == null) {
            if (metadata == null) {
                metadata = new DiskCacheMetadata();
            }

            metadata.setFile(getKeyMetadataFile(key));
            metadata.setCreateTime(System.currentTimeMillis() / 1000L);
            metadata.calculateSize(file);
            metadata.setEntryClass(object.getClass());
            error = metadata.write();
        }

        return error;
    }

    private Serializer getSerializer(Class cl) {
        Serializer serializer = serializerMap.get(cl);
        if (serializer == null && Serializable.class.isAssignableFrom(cl)) {
            serializer = new ObjectSerializer();
        }

        return serializer;
    }

    @Override
    public Object getValue(String key) {
        return ((DiskCacheEntry)getEntry(key)).getObject();
    }

    public Serializable getMetadata(String key) {
        return getEntry(key).getMetadata();
    }

    @Override
    public CacheEntry getEntry(String key) {
        DiskCacheEntry entry = null;
        File file = getKeyFile(key);

        if (!file.exists()) {
            lastError = new Error("DiskCacheProvider getValue: file doesn't exist");
        }

        if (lastError == null) {
            DiskCacheMetadata metadata = null;
            File metadataFile = getKeyMetadataFile(key);
            if (metadataFile.exists()) {
                metadata = DiskCacheMetadata.load(metadataFile);
            }

            Serializer serializer = getSerializer(metadata.getEntryClass());
            assert serializer != null;

            entry = new DiskCacheEntry(file, null, metadata, serializer);
            lastError = entry.load();
        }

        return entry;
    }

    @Override
    public void remove(String key) {
    }

    @Override
    public Error getError() {
        return lastError;
    }
}
