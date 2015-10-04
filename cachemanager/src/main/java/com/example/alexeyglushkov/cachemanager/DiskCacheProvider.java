package com.example.alexeyglushkov.cachemanager;

import com.example.alexeyglushkov.streamlib.InputStreamReader;
import com.example.alexeyglushkov.streamlib.OutputStreamWriter;
import com.noveogroup.android.cache.disk.DiskCache;
import com.noveogroup.android.cache.disk.MetaData;
import com.noveogroup.android.cache.io.Serializer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

/**
 * Created by alexeyglushkov on 26.09.15.
 */
public class DiskCacheProvider implements CacheProvider {

    private File directory;
    private InputStreamReader streamReader;
    private OutputStreamWriter streamWriter;

    public DiskCacheProvider(File directory, InputStreamReader streamReader, OutputStreamWriter streamWriter) {
        this.directory = directory;
        this.streamReader = streamReader;
        this.streamWriter = streamWriter;
    }

    @Override
    public Error store(String key, CacheEntry entry, CacheMetadata metadata) {
        Error error = prepareDirectory();
        if (error == null) {
            error = write(key, entry, metadata);
        }

        return error;
    }

    private Error prepareDirectory() {
        if (!directory.exists()) {
            try {
                directory.createNewFile();
            } catch (IOException ex) {
                return new Error("DiskCacheProvider prepareDirectory exception:" + ex.getMessage());
            }
        }

        return null;
    }

    private File getKeyDirectory(String key) {
        String fileName = Integer.toString(key.hashCode());
        File file = new File(directory.getPath() + File.pathSeparator + fileName);
        return file;
    }

    private Error write(String key, CacheEntry entry, CacheMetadata metadata) {
        Error error = null;
        File file = getKeyDirectory(key);

        if (file.exists()) {
            if(!file.delete()) {
                error = new Error("DiskCacheProvider write delete: can't delete cache file");
            }
        }

        if (error == null) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                error = new Error("DiskCacheProvider write createNewFile exception:" + ex.getMessage());
            }
        }

        if (error == null) {
            OutputStream fos = null;
            try {
                fos = new BufferedOutputStream(new FileOutputStream(file));
                error = streamWriter.writeStream(fos, entry);
            } catch (IOException ex) {
                error = new Error("DiskCacheProvider write open stream exception:" + ex.getMessage());
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (Exception ex) {
                        error = new Error("DiskCacheProvider write close stream exception:" + ex.getMessage());
                    }
                }
            }
        }

        return error;
    }

    @Override
    public CacheEntry getEntry(String key) {
        CacheEntry entry = null;
        File file = getKeyDirectory(key);

        if (file.exists()) {
            InputStream inputStream
            entry = streamReader.readStream();
        }

        return entry;
    }

    public CacheMetadata getMetadata(String key) {
        return null;
    }

    @Override
    public void remove(String key) {
    }
}
