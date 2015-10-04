package com.example.alexeyglushkov.cachemanager;

import com.example.alexeyglushkov.streamlib.InputStreamReader;
import com.example.alexeyglushkov.streamlib.OutputStreamWriter;

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
public class DiskCacheEntry implements CacheEntry {
    File file;
    Object object;

    public DiskCacheEntry(File file) {
        this.file = file;
    }

    @Override
    public Error load() {
        Error error = null;
        InputStream fis = null;

        try {
            fis = new BufferedInputStream(new FileInputStream(file));
            object = handleStream(fis);

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
            error = writer.writeStream(os, object);

        } catch (Exception ex) {
            error = new Error("DiskCacheEntry exception: " + ex.getMessage());
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (Exception ex) {
                    error = new Error("DiskCacheEntry exception: " + ex.getMessage());
                }
            }
        }

        return error;
    }

    protected Object handleStream(InputStream fis) {
        return reader.readStream(fis);
    }
}
