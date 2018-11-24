package com.example.alexeyglushkov.cachemanager.disk;

import androidx.annotation.NonNull;

import com.example.alexeyglushkov.cachemanager.StorageEntry;
import com.example.alexeyglushkov.cachemanager.StorageMetadata;
import com.example.alexeyglushkov.cachemanager.Storage;
import com.example.alexeyglushkov.streamlib.codecs.ObjectCodec;
import com.example.alexeyglushkov.streamlib.codecs.Codec;
import com.example.alexeyglushkov.tools.TimeTools;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
public class DiskStorage implements Storage {
    private static String METADATA_PREFIX = "_metadata";

    private @NonNull File directory;
    private @NonNull Codec defaultCodec = new ObjectCodec();

    private @NonNull Map<String, Codec> keySerializerMap = new HashMap<>();
    private @NonNull Map<Class, Codec> classSerializerMap = new HashMap<>();

    // TODO: need to clear this lockMap somewhere
    private @NonNull Map<String, Object> lockMap = new HashMap<>();

    public DiskStorage(@NonNull File directory) {
        this.directory = directory;
    }

    @Override
    public void put(@NonNull String key, @NonNull Object entry, @Nullable StorageMetadata metadata) throws Exception {
        prepareDirectory();
        write(key, entry, (DiskStorageMetadata) metadata);
    }

    private void prepareDirectory() throws Exception {
        if (!directory.exists()) {
            if (!directory.mkdir()) {
                throw new Exception("DiskStorage.prepareDirectory.mkdir: can't create directory " + directory.getAbsolutePath());
            }
        }
    }

    public File getKeyFile(@NonNull String key) {
        return new File(directory.getPath() + File.separator + key);
    }

    @NonNull
    private File getKeyMetadataFile(@NonNull String key) {
        String fileName = key + METADATA_PREFIX;
        return new File(directory.getPath() + File.separator + fileName);
    }

    private boolean isMetadataFile(@NonNull File file) {
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
            '/','|','"','?','*','<','>','\\',':','+','[',']'
    ));

    private String getKeyName(@NonNull String fileName) {
        StringBuilder builder = new StringBuilder(fileName);
        for (int i = 0; i < builder.length(); i++) {
            char c = builder.charAt(i);
            if (ReservedCharsSet.contains(c)) {
                builder.replace(i,i+1,"_");
            }
        }

        return builder.toString();
    }

    synchronized private Object getLockObject(@NonNull String key) {
        Object lockObject = lockMap.get(key);
        if (lockObject == null) {
            lockObject = createLockObject(key);
        }
        return lockObject;
    }

    private Object createLockObject(@NonNull String key) {
        Object lockObject = new Object();
        lockMap.put(key, lockObject);
        return lockObject;
    }

    // TODO: need to refactor
    private void writeByKey(@NonNull String key, @NonNull Object object, @Nullable DiskStorageMetadata metadata) throws Exception {
        File file = getKeyFile(key);
        if (!file.exists()) {
            createFile(file);
        }

        try {
            Codec codec = getSerializer(key, object.getClass());
            if (codec == null) {
                throw new Exception("Can't find a serializer for " + object.getClass());
            }

            DiskStorageEntry entry = new DiskStorageEntry(file, object, metadata, codec);
            entry.write();

            if (metadata != null) {
                writeMetadata(metadata, object, key, file);
            }

        } catch (Exception ex) {
            file.delete();
            if (metadata != null && metadata.getFile() != null) {
                metadata.getFile().delete();
            }

            throw ex;
        }
    }

    private void createFile(@NonNull File file) throws IOException {
        boolean isFileCreated = file.createNewFile();
        if (!isFileCreated) {
            throw new IOException("DiskStorage.write() createNewFile create error");
        }
    }

    private Codec getSerializer(@Nullable String key, @Nullable Class cl) {
        Codec codec = key != null ? keySerializerMap.get(key) : null;
        if (codec == null) {
            codec = cl != null ? classSerializerMap.get(cl) : null;
        }
        return codec == null ? defaultCodec : codec;
    }

    private void writeMetadata(@NonNull DiskStorageMetadata metadata, @NonNull Object object, @NonNull String key, @NonNull File file) throws Exception {
        metadata.setFile(getKeyMetadataFile(key));
        metadata.setCreateTime(TimeTools.currentTimeSeconds());
        metadata.calculateSize(file);
        metadata.setEntryClass(object.getClass());
        metadata.write();
    }

    @Override
    @Nullable
    public Object getValue(@NonNull  String key) throws Exception {
        Object result = null;
        DiskStorageEntry entry = (DiskStorageEntry)getEntry(key);

        if (entry != null) {
            result = entry.getObject();
        }

        return result;
    }

    @Override
    @NonNull
    public DiskStorageMetadata createMetadata() {
        return new DiskStorageMetadata();
    }

    @Nullable
    public StorageMetadata getMetadata(@NonNull String key) throws Exception {
        StorageMetadata result = null;
        StorageEntry entry = getEntry(key);
        if (entry != null) {
            result = entry.getMetadata();
        }
        return result;
    }

    @Override
    @Nullable
    public StorageEntry getEntry(@NonNull String fileName) throws Exception {
        StorageEntry entry = null;
        String key = getKeyName(fileName);
        Object lockObject = getLockObject(key);

        synchronized (lockObject) {
            entry = getEntryByKey(key);
        }

        return entry;
    }

    @NonNull
    private StorageEntry getEntryByKey(@NonNull String key) throws Exception {
        DiskStorageEntry entry = null;
        File file = getKeyFile(key);

        if (!file.exists()) {
            throw new FileNotFoundException("DiskStorage.getEntryByKey() exists(): file doesn't exist");
        }

        DiskStorageMetadata metadata = null;
        Codec codec = null;
        File metadataFile = getKeyMetadataFile(key);

        if (metadataFile.exists()) {
            try {
                metadata = DiskStorageMetadata.load(metadataFile);
                if (metadata != null) {
                    codec = getSerializer(key, metadata.getEntryClass());
                } else {
                    codec = defaultCodec;
                }

            } catch (Exception e) {
                codec = defaultCodec;
            }
        } else {
            codec = defaultCodec;
        }

        if (codec == null) {
            throw new Exception("Serializer is null");
        }

        entry = new DiskStorageEntry(file, null, metadata, codec);
        return entry;
    }

    @Override
    public void remove(@NonNull String key) throws Exception {
        Object lockObject = getLockObject(key);
        synchronized (lockObject) {
            StorageEntry entry = getEntry(key);
            if (entry != null) {
                entry.delete();
            }
        }
    }

    @Override
    public List<StorageEntry> getEntries() throws Exception {
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

    public void removeAll() throws Exception {
        List<StorageEntry> entries = getEntries();
        for (StorageEntry file : entries) {
            DiskStorageEntry diskCacheEntry = (DiskStorageEntry)file;
            String key = diskCacheEntry.getFileName();
            Object lockObject = getLockObject(key);
            synchronized (lockObject) {
                file.delete();
            }
        }

        if (!directory.delete()) {
            throw new Exception("DiskStorage.removeAll() delete(): remove directory error");
        }
    }

    //// Setter

    public void setSerializer(@NonNull Codec codec, @NonNull Class cl) {
        classSerializerMap.put(cl, codec);
    }

    public void setSerializer(@NonNull Codec codec, @NonNull String key) {
        keySerializerMap.put(key, codec);
    }

    public void setDefaultCodec(@NonNull Codec defaultCodec) {
        this.defaultCodec = defaultCodec;
    }

    //// Getter

    @NonNull
    public File getDirectory() {
        return directory;
    }
}
