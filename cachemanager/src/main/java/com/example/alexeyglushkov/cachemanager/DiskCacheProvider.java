package com.example.alexeyglushkov.cachemanager;

import android.util.Log;
import android.util.SparseArray;

import com.example.alexeyglushkov.streamlib.serializers.ObjectSerializer;
import com.example.alexeyglushkov.streamlib.serializers.Serializer;

import junit.framework.Assert;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

/**
 * Created by alexeyglushkov on 26.09.15.
 */

// TODO: check synchronization
public class DiskCacheProvider implements CacheProvider {
    private static String ERROR_TAG = "DiskCacheProvider error";
    private static String METADATA_PREFIX = "_metadata";

    private File directory;
    private Map<Class,Serializer> serializerMap;
    private Error lastError;

    private Map<String, WeakReference<Object>> lockMap = new HashMap<>();

    public DiskCacheProvider(File directory) {
        this.directory = directory;
        this.serializerMap = new HashMap<>();
    }

    public void setSerializer(Serializer serializer, Class cl) {
        serializerMap.put(cl, serializer);
    }

    @Override
    public Error put(String key, Object entry, CacheMetadata metadata) {
        Error error = prepareDirectory();
        if (error == null) {
            error = write(key, entry, (DiskCacheMetadata) metadata);
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

    private File getKeyFile(String key) {
        return new File(directory.getPath() + File.separator + key);
    }

    private File getKeyMetadataFile(String key) {
        String fileName = key + METADATA_PREFIX;
        return new File(directory.getPath() + File.separator + fileName);
    }

    private boolean isMetadataFile(File file) {
        return file.getName().endsWith(METADATA_PREFIX);
    }

    private Error write(String fileName, Object object, DiskCacheMetadata metadata) {
        Error error = null;
        String key = getKeyName(fileName);
        Object lockObject = getLockObject(key);
        synchronized (lockObject) {
            error = writeByKey(key, object, metadata);
        }

        return error;
    }

    private static final Set<Character> ReservedCharsSet = new HashSet<>(Arrays.asList(
            new Character[] {'/','|','"','?','*','<','>','\\',':','+','[',']'}
    ));

    private String getKeyName(String fileName) {
        StringBuilder builder = new StringBuilder(fileName);
        for (int i = 0; i < builder.length(); i++) {
            char c = builder.charAt(i);
            if (ReservedCharsSet.contains(c)) {
                builder.replace(i,i+1,"_");
            }
        }

        return builder.toString();
    }

    synchronized private Object getLockObject(String key) {
        WeakReference<Object> lockObjectRef = lockMap.get(key);
        Object lockObject = null;
        if (lockObjectRef != null) {
            lockObject = lockObjectRef.get();
            if (lockObject == null) {
                lockObject = createLockObject(key);
            }
        } else {
            lockObject = createLockObject(key);
        }
        return lockObject;
    }

    private Object createLockObject(String key) {
        Object lockObject;
        lockObject = new Object();
        lockMap.put(key, new WeakReference<>(lockObject));
        return lockObject;
    }

    private Error writeByKey(String key, Object object, DiskCacheMetadata metadata) {
        Error error = null;

        File file = getKeyFile(key);
        if (file.exists()) {
            if (!file.delete()) {
                error = new Error("DiskCacheProvider.writeByKey() delete: can't delete cache file");
                setLastError(error);
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
                metadata = createMetadata();
            }

            metadata.setFile(getKeyMetadataFile(key));
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
        Object result = null;
        DiskCacheEntry entry = (DiskCacheEntry)getEntry(key);

        if (entry != null) {
            result = entry.getObject();
        }

        return result;
    }

    @Override
    public DiskCacheMetadata createMetadata() {
        return new DiskCacheMetadata();
    }

    public CacheMetadata getMetadata(String key) {
        CacheMetadata result = null;
        CacheEntry entry = getEntry(key);
        if (entry != null) {
            result = entry.getMetadata();
        }
        return result;
    }

    @Override
    public CacheEntry getEntry(String fileName) {
        CacheEntry entry = null;
        String key = getKeyName(fileName);
        Object lockObject = getLockObject(key);
        synchronized (lockObject) {
            entry = getEntryByKey(key);
        }

        return entry;
    }

    private CacheEntry getEntryByKey(String key) {
        DiskCacheEntry entry = null;
        File file = getKeyFile(key);
        Error error = null;

        if (!file.exists()) {
            error = new Error("DiskCacheProvider.getEntryByKey() exists(): file doesn't exist");
        }

        if (error == null) {
            DiskCacheMetadata metadata = null;
            File metadataFile = getKeyMetadataFile(key);
            if (metadataFile.exists()) {
                metadata = DiskCacheMetadata.load(metadataFile);

                Serializer serializer = getSerializer(metadata.getEntryClass());
                assert serializer != null;

                entry = new DiskCacheEntry(file, null, metadata, serializer);
            } else {
                error = new Error("DiskCacheProvider.getEntryByKey() exists(): metadata doesn't exist");
                setLastError(error);
            }
        }

        return entry;
    }

    @Override
    public Error remove(String key) {
        Error error = null;
        Object lockObject = getLockObject(key);
        synchronized (lockObject) {
            CacheEntry entry = getEntry(key);
            if (entry != null) {
                error = entry.delete();
                setLastError(error);
            }
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
                String key = file.getName();
                Object lockObject = getLockObject(key);
                synchronized (lockObject) {
                    CacheEntry entry = getEntryByKey(key);
                    if (entry != null) {
                        entries.add(entry);
                    }
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
            DiskCacheEntry diskCacheEntry = (DiskCacheEntry)file;
            String key = diskCacheEntry.getFileName();
            Object lockObject = getLockObject(key);
            synchronized (lockObject) {
                Error deleteError = file.delete();
                if (deleteError == null) {
                    error = deleteError;
                    setLastError(error);
                }
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
