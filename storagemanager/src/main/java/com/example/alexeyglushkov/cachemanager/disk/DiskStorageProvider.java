package com.example.alexeyglushkov.cachemanager.disk;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.alexeyglushkov.cachemanager.StorageEntry;
import com.example.alexeyglushkov.cachemanager.StorageMetadata;
import com.example.alexeyglushkov.cachemanager.StorageProvider;
import com.example.alexeyglushkov.streamlib.serializers.ObjectSerializer;
import com.example.alexeyglushkov.streamlib.serializers.Serializer;

import android.support.annotation.Nullable;

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

/**
 * Created by alexeyglushkov on 26.09.15.
 */

// TODO: check synchronization
public class DiskStorageProvider implements StorageProvider {
    private static String ERROR_TAG = "DiskStorageProvider error";
    private static String METADATA_PREFIX = "_metadata";

    private File directory;
    private Serializer defaultSerializer = new ObjectSerializer();
    private Map<Class, Serializer> serializerMap;
    private Error lastError;

    private Map<String, WeakReference<Object>> lockMap = new HashMap<>();

    public DiskStorageProvider(File directory) {
        this.directory = directory;
        this.serializerMap = new HashMap<>();
    }

    @Override
    public void put(String key, Object entry, StorageMetadata metadata) throws Exception {
        prepareDirectory();
        write(key, entry, (DiskStorageMetadata) metadata);
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

    private void prepareDirectory() throws Exception {
        if (!directory.exists()) {
            if (!directory.mkdir()) {
                throw new Exception("DiskStorageProvider.prepareDirectory.mkdir: can't create directory " + directory.getAbsolutePath());
            }
        }
    }

    public File getKeyFile(String key) {
        return new File(directory.getPath() + File.separator + key);
    }

    private File getKeyMetadataFile(String key) {
        String fileName = key + METADATA_PREFIX;
        return new File(directory.getPath() + File.separator + fileName);
    }

    private boolean isMetadataFile(File file) {
        return file.getName().endsWith(METADATA_PREFIX);
    }

    private void write(@NonNull String fileName, @NonNull Object object, @Nullable DiskStorageMetadata metadata) throws Exception {
        String key = getKeyName(fileName);
        Object lockObject = getLockObject(key);

        synchronized (lockObject) {
            writeByKey(key, object, metadata);
        }
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

    // TODO: need to refactor
    private void writeByKey(@NonNull String key, @NonNull Object object, @Nullable DiskStorageMetadata metadata) throws Exception {
        File file = getKeyFile(key);
        if (!file.exists()) {
            createFile(file);
        }

        try {
            Serializer serializer = getSerializer(object.getClass());
            if (serializer == null) {
                throw new Exception("Can't find a serializer for " + object.getClass());
            }

            DiskStorageEntry entry = new DiskStorageEntry(file, object, metadata, serializer);
            entry.write();

            if (metadata != null) {
                writeMetadata(metadata, object, key, file);
            }

        } finally {
            file.delete();
            if (metadata != null && metadata.getFile() != null) {
                metadata.getFile().delete();
            }
        }
    }

    private void createFile(File file) throws IOException {
        boolean isFileCreated = file.createNewFile();
        if (!isFileCreated) {
            throw new IOException("DiskStorageProvider.write() createNewFile create error");
        }
    }

    private Serializer getSerializer(Class cl) {
        Serializer serializer = serializerMap.get(cl);
        return serializer == null ? defaultSerializer : serializer;
    }

    private void writeMetadata(@NonNull DiskStorageMetadata metadata, @NonNull Object object, @NonNull String key, @NonNull File file) throws Exception {
        metadata.setFile(getKeyMetadataFile(key));
        metadata.setCreateTime(System.currentTimeMillis() / 1000L);
        metadata.calculateSize(file);
        metadata.setEntryClass(object.getClass());
        metadata.write();
    }

    @Override
    @Nullable
    public Object getValue(String key) {
        Object result = null;
        DiskStorageEntry entry = (DiskStorageEntry)getEntry(key);

        if (entry != null) {
            try {
                result = entry.getObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    @Override
    public DiskStorageMetadata createMetadata() {
        return new DiskStorageMetadata();
    }

    public StorageMetadata getMetadata(String key) {
        StorageMetadata result = null;
        StorageEntry entry = getEntry(key);
        if (entry != null) {
            result = entry.getMetadata();
        }
        return result;
    }

    @Override
    public StorageEntry getEntry(String fileName) {
        StorageEntry entry = null;
        String key = getKeyName(fileName);
        Object lockObject = getLockObject(key);
        synchronized (lockObject) {
            entry = getEntryByKey(key);
        }

        return entry;
    }

    private StorageEntry getEntryByKey(String key) {
        DiskStorageEntry entry = null;
        File file = getKeyFile(key);
        Error error = null;

        if (!file.exists()) {
            error = new Error("DiskStorageProvider.getEntryByKey() exists(): file doesn't exist");
            setLastError(error);
        }

        if (error == null) {
            DiskStorageMetadata metadata = null;
            File metadataFile = getKeyMetadataFile(key);
            if (metadataFile.exists()) {
                Serializer serializer = null;
                try {
                    metadata = DiskStorageMetadata.load(metadataFile);
                    serializer = getSerializer(metadata.getEntryClass());

                } catch (Exception e) {
                    serializer = defaultSerializer;
                }

                Assert.assertTrue(serializer != null);

                entry = new DiskStorageEntry(file, null, metadata, serializer);

            } else {
                error = new Error("DiskStorageProvider.getEntryByKey() exists(): metadata doesn't exist");
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
            StorageEntry entry = getEntry(key);
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

    // TODO: think about returning interface instead
    @Override
    public List<StorageEntry> getEntries() {
        List<StorageEntry> entries = new ArrayList<>();
        if (!directory.exists()) {
            return entries;
        }

        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (!isMetadataFile(file)) {
                    String key = file.getName();
                    Object lockObject = getLockObject(key);
                    synchronized (lockObject) {
                        StorageEntry entry = getEntryByKey(key);
                        if (entry != null) {
                            entries.add(entry);
                        }
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
        List<StorageEntry> entries = getEntries();
        for (StorageEntry file : entries) {
            DiskStorageEntry diskCacheEntry = (DiskStorageEntry)file;
            String key = diskCacheEntry.getFileName();
            Object lockObject = getLockObject(key);
            synchronized (lockObject) {
                Error deleteError = file.delete();
                if (deleteError != null) {
                    error = deleteError;
                    setLastError(error);
                }
            }
        }

        if (error == null) {
            if (!directory.delete()) {
                error = new Error("DiskStorageProvider.removeAll() delete(): remove directory error");
                setLastError(error);
            }
        }

        return error;
    }

    //// Setter

    public void setSerializer(Serializer serializer, Class cl) {
        serializerMap.put(cl, serializer);
    }

    public void setDefaultSerializer(Serializer defaultSerializer) {
        this.defaultSerializer = defaultSerializer;
    }

    //// Getter

    public File getDirectory() {
        return directory;
    }
}
