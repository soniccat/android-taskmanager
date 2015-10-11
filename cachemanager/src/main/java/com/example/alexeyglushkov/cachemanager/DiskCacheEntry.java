package com.example.alexeyglushkov.cachemanager;

import com.example.alexeyglushkov.streamlib.serializers.Serializer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * Created by alexeyglushkov on 27.09.15.
 */
public class DiskCacheEntry implements CacheEntry {
    private File file;
    private Object object;
    private Serializer serializer;
    private DiskCacheMetadata metadata;

    public DiskCacheEntry(File file, Object object, DiskCacheMetadata metadata, Serializer serializer) {
        this.file = file;
        this.object = object;
        this.metadata = metadata;
        this.serializer = serializer;
    }

    public Object getObject() {
        return object;
    }

    public Error load() {
        Error error = null;
        if (object == null) {
            error = loadObject();
        }

        return error;
    }

    private Error loadObject() {
        Error error = null;
        InputStream fis = null;

        try {
            fis = new BufferedInputStream(new FileInputStream(file));
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

        if (error == null) {
            error = DiskCacheMetadata.delete(metadata.getFile());
        }

        return error;
    }

    @Override
    public Serializable getMetadata() {
        return metadata;
    }
}
