package com.example.alexeyglushkov.cachemanager.disk;

import androidx.annotation.NonNull;

import com.example.alexeyglushkov.cachemanager.StorageEntry;
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.InputStreamDataReaders;
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.OutputStreamDataWriters;
import com.example.alexeyglushkov.streamlib.codecs.Codec;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 27.09.15.
 */
public class DiskStorageEntry implements StorageEntry {
    @NonNull  private File file;
    @Nullable private Object object;
    @NonNull  private Codec codec;
    @Nullable private DiskStorageMetadata metadata;

    public DiskStorageEntry(@NonNull File file, @Nullable Object object, @Nullable DiskStorageMetadata metadata, @NonNull Codec codec) {
        this.file = file;
        this.object = object;
        this.metadata = metadata;
        this.codec = codec;
    }

    public String getFileName() {
        return file.getName();
    }

    @NonNull
    public Object getObject() throws Exception {
        if (object == null) {
            loadObject();
        }

        return object;
    }

    private void loadObject() throws Exception {
        InputStream stream = new FileInputStream(file);
        object = InputStreamDataReaders.readOnce(codec, stream);
    }

    public void write() throws Exception {
        OutputStream os = new FileOutputStream(file);
        OutputStreamDataWriters.writeOnce(codec, os, object);
    }

    @Override
    public void delete() throws Exception {
        if (!file.delete()) {
            throw new Exception("DiskCacheEntry delete: can't delete file " + file.getAbsolutePath());
        }

        if (metadata != null) {
            File file = metadata.getFile();
            if (file != null && !metadata.getFile().delete()) {
                throw new Exception("DiskCacheEntry delete: can't delete metadata " + metadata.getFile().getAbsolutePath());
            }
        }
    }

    @Override
    @Nullable
    public DiskStorageMetadata getMetadata() {
        return metadata;
    }
}
