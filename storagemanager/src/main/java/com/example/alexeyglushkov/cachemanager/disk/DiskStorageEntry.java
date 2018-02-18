package com.example.alexeyglushkov.cachemanager.disk;

import android.support.annotation.NonNull;

import com.example.alexeyglushkov.cachemanager.StorageEntry;
import com.example.alexeyglushkov.streamlib.readersandwriters.InputStreamReaders;
import com.example.alexeyglushkov.streamlib.readersandwriters.OutputStreamWriters;
import com.example.alexeyglushkov.streamlib.serializers.Serializer;

import android.support.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 27.09.15.
 */
public class DiskStorageEntry implements StorageEntry {
    @NonNull  private File file;
    @Nullable private Object object;
    @NonNull  private Serializer serializer;
    @Nullable private DiskStorageMetadata metadata;

    public DiskStorageEntry(@NonNull File file, @Nullable Object object, @Nullable DiskStorageMetadata metadata, @NonNull Serializer serializer) {
        this.file = file;
        this.object = object;
        this.metadata = metadata;
        this.serializer = serializer;
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
        InputStream stream = new BufferedInputStream(new FileInputStream(file));
        object = InputStreamReaders.readOnce(serializer, stream);
    }

    public void write() throws Exception {
        OutputStream os = new BufferedOutputStream(new FileOutputStream(file));;
        OutputStreamWriters.writeOnce(serializer, os, object);
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
