package com.example.alexeyglushkov.cachemanager.disk;

import android.support.annotation.NonNull;

import com.example.alexeyglushkov.cachemanager.StorageEntry;
import com.example.alexeyglushkov.streamlib.serializers.Serializer;

import android.support.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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


    // TODO: consider throwing an exception
    public Object getObject() {
        if (object == null) {
            loadObject();
        }

        return object;
    }

    private Error loadObject() {
        Error error = null;
        InputStream fis = null;

        try {
            fis = new BufferedInputStream(new FileInputStream(file));
            //StringReader tstReader = new StringReader(null);
            //String str = tstReader.readStreamToString(fis);
            //Log.d("Parsing", str);

            object = serializer.read(fis);
            error = serializer.getReadError();

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

        return error;
    }

    // TODO: consider throwing an exception
    public Error write() {
        Error error = null;
        OutputStream os = null;

        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            error = serializer.write(os, object);

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

    @Override
    public Error delete() {
        Error error = null;
        if (!file.delete()) {
            error = new Error("DiskCacheEntry delete: can't delete file " + file.getAbsolutePath());
        }

        if (error == null && metadata != null) {
            error = DiskStorageMetadata.delete(metadata.getFile());
        }

        return error;
    }

    @Override
    @Nullable
    public DiskStorageMetadata getMetadata() {
        return metadata;
    }
}
