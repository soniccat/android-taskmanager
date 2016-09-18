package com.example.alexeyglushkov.cachemanager.disk.serializer;

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
    private Error error;

    @Override
    public Object readStream(InputStream data) {
        error = null;

        Object result = null;
        try {
            SimpleModule md = new SimpleModule("DiskMetadataModule", new Version(1,0,0,null,null,null));
            md.addDeserializer(DiskStorageMetadata.class, new DiskMetadataDeserializer(DiskStorageMetadata.class));

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(md);

            result = mapper.readValue(data, DiskStorageMetadata.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public void setProgressUpdater(ProgressUpdater progressUpdater) {

    }

    @Override
    public Error getError() {
        return error;
    }
}
