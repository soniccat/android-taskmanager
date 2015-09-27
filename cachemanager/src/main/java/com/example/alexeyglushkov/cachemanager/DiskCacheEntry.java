package com.example.alexeyglushkov.cachemanager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by alexeyglushkov on 27.09.15.
 */
public class DiskCacheEntry implements CacheEntry {
    File file;
    InputStreamReader reader;
    Object object;

    public DiskCacheEntry(File file, InputStreamReader reader) {
        this.file = file;
        this.reader = reader;
    }

    @Override
    public Error load() {
        Error error = null;
        InputStream fis = null;

        try {
            fis = new BufferedInputStream(new FileInputStream(file));
            object = handleStream(fis);

        } catch (Exception ex) {
            error = new Error(ex.getMessage());
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception ex) {
                    error = new Error(ex.getMessage());
                }
            }
        }

        return error;
    }

    protected Object handleStream(InputStream fis) {
        return reader.readStream(fis);
    }
}
