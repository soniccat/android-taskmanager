package com.example.alexeyglushkov.cachemanager.disk.serializer;

import android.support.annotation.NonNull;

import com.example.alexeyglushkov.cachemanager.disk.DiskStorageMetadata;
import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;
import com.example.alexeyglushkov.streamlib.readersandwriters.InputStreamReader;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by alexeyglushkov on 11.09.16.
 */
public class DiskMetadataReader implements InputStreamReader {

    @Override
    public @NonNull
    void beginRead(@NonNull InputStream stream) {
        return stream;
    }

    @Override
    public Object read() throws IOException {
        Object result = null;
        SimpleModule md = new SimpleModule("DiskMetadataModule", new Version(1,0,0,null,null,null));
        md.addDeserializer(DiskStorageMetadata.class, new DiskMetadataDeserializer(DiskStorageMetadata.class));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(md);

        result = mapper.readValue(data, DiskStorageMetadata.class);
        return result;
    }

    @Override
    public void setProgressUpdater(@NonNull ProgressUpdater progressUpdater) {

    }
}
