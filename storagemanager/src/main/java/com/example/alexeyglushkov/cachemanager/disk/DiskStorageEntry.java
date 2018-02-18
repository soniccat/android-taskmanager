package com.example.alexeyglushkov.cachemanager.disk;

import android.support.annotation.NonNull;

import com.example.alexeyglushkov.cachemanager.StorageEntry;
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
        InputStream fis = null;

        try {
            fis = new BufferedInputStream(new FileInputStream(file));
            //StringReader tstReader = new StringReader(null);
            //String str = tstReader.readStreamToString(fis);
            //Log.d("Parsing", str);

            object = serializer.read(fis);

        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception ex) {
                }
            }
        }
    }

    public void write() throws Exception {


        OutputStream os = null;

        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            OutputStreamWriters.writeOnce(serializer, os, object);
            serializer.write(os, object);

        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (Exception ex) {
                }
            }
        }
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
