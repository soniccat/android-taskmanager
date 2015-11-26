package com.example.alexeyglushkov.cachemanager;

import android.util.Log;

import com.example.alexeyglushkov.streamlib.serializers.ObjectSerializer;
import com.example.alexeyglushkov.streamlib.serializers.Serializer;

import junit.framework.Assert;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alexeyglushkov on 26.09.15.
 */
public class DiskCacheProvider implements CacheProvider {
    private static String ERROR_TAG = "DiskCacheProvider error";
    private static String METADATA_PREFIX = "_metadata";

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
            setLastError(error);
        }

        return error;
    }

    private void setLastError(Error error) {
        if (error != null) {
            this.lastError = error;
            logError(error);
        }
    }

    private void logError(Error error) {
        Log.e(ERROR_TAG, error.getMessage());
    }

    private Error prepareDirectory() {
        Error error = null;
        if (!directory.exists()) {
            try {
                directory.createNewFile();
            } catch (IOException ex) {
                error = new Error("DiskCacheProvider.prepareDirectory() createNewFile exception:" + ex.getMessage());
                setLastError(error);
            }
        }

        return error;
    }

    private File getKeyFile(int hash) {
        String fileName = Integer.toString(hash);
        File file = new File(directory.getPath() + File.separator + fileName);
        return file;
    }

    private File getKeyMetadataFile(int hash) {
        String fileName = Integer.toString(hash) + METADATA_PREFIX;
        File file = new File(directory.getPath() + File.separator + fileName);
        return file;
    }

    private boolean isMetadataFile(File file) {
        return file.getName().endsWith(METADATA_PREFIX);
    }

    private Error write(String key, Object object, DiskCacheMetadata metadata) {
        return writeByHash(key.hashCode(), object, metadata);
    }

    private Error writeByHash(int hash, Object object, DiskCacheMetadata metadata) {
        Error error = null;

        File file = null;
        if (error == null) {
            file = getKeyFile(hash);
            if (file.exists()) {
                if (!file.delete()) {
                    error = new Error("DiskCacheProvider.writeByHash() delete: can't delete cache file");
                    setLastError(error);
                }
            }
        }

        if (error == null) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                error = new Error("DiskCacheProvider.write() createNewFile exception:" + ex.getMessage());
                setLastError(error);
            }
        }

        if (error == null) {
            Serializer serializer = getSerializer(object.getClass());
            Assert.assertTrue("Can't find a serializer for " + object.getClass(), serializer != null);

            DiskCacheEntry entry = new DiskCacheEntry(file, object, metadata, serializer);
            error = entry.write();
            setLastError(error);
        }

        if (error == null) {
            if (metadata == null) {
                metadata = new DiskCacheMetadata();
            }

            metadata.setFile(getKeyMetadataFile(hash));
            metadata.setCreateTime(System.currentTimeMillis() / 1000L);
            metadata.calculateSize(file);
            metadata.setEntryClass(object.getClass());
            error = metadata.write();
            setLastError(error);

        } else if (file != null) {
            file.delete();
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
        DiskCacheEntry entry = (DiskCacheEntry)getEntry(key);

        if (entry != null) {
            return entry.getObject();
        }
        return null;
    }

    public Serializable getMetadata(String key) {
        return getEntry(key).getMetadata();
    }

    @Override
    public CacheEntry getEntry(String key) {
        return getEntryByHash(key.hashCode());
    }

    private CacheEntry getEntryByHash(int hash) {
        DiskCacheEntry entry = null;
        File file = getKeyFile(hash);
        Error error = null;

        if (!file.exists()) {
            error = new Error("DiskCacheProvider.getEntryByHash() exists(): file doesn't exist");
            setLastError(error);
        }

        if (error == null) {
            DiskCacheMetadata metadata = null;
            File metadataFile = getKeyMetadataFile(hash);
            if (metadataFile.exists()) {
                metadata = DiskCacheMetadata.load(metadataFile);

                Serializer serializer = getSerializer(metadata.getEntryClass());
                assert serializer != null;

                entry = new DiskCacheEntry(file, null, metadata, serializer);
            } else {
                error = new Error("DiskCacheProvider.getEntryByHash() exists(): metadata doesn't exist");
                setLastError(error);
            }
        }

        return entry;
    }

    @Override
    public Error remove(String key) {
        Error error = null;
        CacheEntry entry = getEntry(key);
        if (entry != null) {
            error = entry.delete();
            setLastError(error);
        }
        return error;
    }

    @Override
    public Error getError() {
        return lastError;
    }

    @Override
    public List<CacheEntry> getEntries() {
        List<CacheEntry> entries = new ArrayList<>();
        if (!directory.exists()) {
            return entries;
        }

        File[] files = directory.listFiles();

        for (File file : files) {
            if (!isMetadataFile(file)) {
                int hash = Integer.parseInt(file.getName());
                CacheEntry entry = getEntryByHash(hash);
                if (entry != null) {
                    entries.add(entry);
                }
            }
        }

        return entries;
    }

    public int getEntryCount() {
        File[] files = directory.listFiles();
        int entryCount = 0;

        for (File file : files) {
            if (!isMetadataFile(file)) {
                ++entryCount;
            }
        }

        return entryCount;
    }

    public Error removeAll() {
        Error error = null;
        List<CacheEntry> entries = getEntries();
        for (CacheEntry file : entries) {
            Error deleteError = file.delete();
            if (deleteError == null) {
                error = deleteError;
                setLastError(error);
            }
        }

        if (error == null) {
            if (!directory.delete()) {
                error = new Error("DiskCacheProvider.removeAll() delete(): remove directory error");
                setLastError(error);
            }
        }

        return error;
    }
}
