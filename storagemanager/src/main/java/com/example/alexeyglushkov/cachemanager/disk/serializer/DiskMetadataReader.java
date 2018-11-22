package com.example.alexeyglushkov.cachemanager.disk.serializer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.alexeyglushkov.cachemanager.disk.DiskStorageMetadata;
import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.InputStreamDataReader;
import com.example.alexeyglushkov.tools.ExceptionTools;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by alexeyglushkov on 11.09.16.
 */
public class DiskMetadataReader implements InputStreamDataReader<DiskStorageMetadata> {
    private @Nullable InputStream stream;

    @Override
    public void beginRead(@NonNull InputStream stream) {
        ExceptionTools.throwIfNull(stream, "DiskMetadataReader.beginRead: stream is null");
        this.stream = new BufferedInputStream(stream);
    }

    @Override
    public void closeRead() throws Exception {
        ExceptionTools.throwIfNull(stream, "DiskMetadataReader.close: stream is null");
        stream.close();
    }

    @Override
    public DiskStorageMetadata read() throws IOException {
        ExceptionTools.throwIfNull(stream, "DiskMetadataReader.read: stream is null");

        DiskStorageMetadata result = null;
        SimpleModule md = new SimpleModule("DiskMetadataModule", new Version(1,0,0,null,null,null));
        md.addDeserializer(DiskStorageMetadata.class, new DiskMetadataDeserializer(DiskStorageMetadata.class));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(md);

        result = mapper.readValue(stream, DiskStorageMetadata.class);
        return result;
    }

    @Override
    public void setProgressUpdater(@NonNull ProgressUpdater progressUpdater) {
    }
}
