package com.example.alexeyglushkov.cachemanager.disk;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.alexeyglushkov.cachemanager.StorageMetadata;
import com.example.alexeyglushkov.cachemanager.disk.serializer.DiskMetadataCodec;
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.InputStreamDataReaders;
import com.example.alexeyglushkov.streamlib.codecs.Codec;
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.OutputStreamDataWriters;
import com.example.alexeyglushkov.tools.TimeTools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 04.10.15.
 */
public class DiskStorageMetadata implements StorageMetadata
{
    private long contentSize;
    private long createDate;
    private long expireDate;
    private @Nullable Class entryClass;
    private @Nullable File file;
    private @NonNull
    Codec codec = createSerializer();

    //// Initialization

    public DiskStorageMetadata() {
    }

    //// Actions

    public void write() throws Exception {
        OutputStreamDataWriters.writeOnce(codec, new FileOutputStream(file), this);
    }

    public static DiskStorageMetadata load(File file) throws Exception {
        DiskStorageMetadata result = null;

        Codec codec = createSerializer();
        result = (DiskStorageMetadata) InputStreamDataReaders.readOnce(codec, new FileInputStream(file));
        result.setFile(file);
        return result;
    }

    public void calculateSize(File file) {
        contentSize = file.length();
    }

    //// Construction Methods

    private static Codec createSerializer() {
        return new DiskMetadataCodec();
    }

    //// Interfaces

    // StorageMetadata

    public long getContentSize() {
        return contentSize;
    }

    public void setContentSize(long fileSize) {
        contentSize = fileSize;
    }

    public long getCreateTime() {
        return createDate;
    }

    public void setCreateTime(long createTime) {
        this.createDate = createTime;
    }

    public long getExpireTime() {
        return expireDate;
    }

    @Override
    public boolean isExpired() {
        boolean isExpired = hasExpireTime() && TimeTools.currentTimeSeconds() >= getExpireTime();
        return isExpired;
    }

    public boolean hasExpireTime() {
        return expireDate > 0;
    }

    public void setExpireTime(long expireTime) {
        this.expireDate = expireTime;
    }

    public void setEntryClass(@NonNull Class cl) {
        entryClass = cl;
    }

    @Nullable
    public Class getEntryClass() {
        return entryClass;
    }

    //// Setters

    public void setFile(@NonNull File file) {
        this.file = file;
    }

    //// Getters

    @Nullable
    public File getFile() {
        return file;
    }
}
