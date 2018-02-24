package com.example.alexeyglushkov.cachemanager.disk.serializer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.alexeyglushkov.cachemanager.disk.DiskStorageMetadata;
import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.OutputStreamDataWriter;
import com.example.alexeyglushkov.tools.ExceptionTools;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 11.09.16.
 */
public class DiskMetadataWriter implements OutputStreamDataWriter {
    private @Nullable OutputStream stream;

    @Override
    public void beginWrite(@NonNull OutputStream stream) {
        ExceptionTools.throwIfNull(stream, "DiskMetadataReader.beginWrite: stream is null");
        this.stream = stream;
    }

    @Override
    public void closeWrite() throws IOException {
        ExceptionTools.throwIfNull(stream, "DiskMetadataReader.close: stream is null");
        stream.close();
    }

    @Override
    public void write(@NonNull Object object) throws IOException {
        ExceptionTools.throwIfNull(stream, "DiskMetadataReader.write: stream is null");

        DiskStorageMetadata metadata = (DiskStorageMetadata)object;

        JsonFactory f = new JsonFactory();
        JsonGenerator g = null;
        try {
            g = f.createGenerator(stream);
            g.writeStartObject();
            g.writeNumberField("contentSize", metadata.getContentSize());
            g.writeNumberField("createTime", metadata.getCreateTime());
            g.writeNumberField("expireTime", metadata.getExpireTime());

            Class entryClass = metadata.getEntryClass();
            if (entryClass != null) {
                g.writeStringField("entryClass", entryClass.getName());
            }

        } finally {
            if (g != null) {
                try {
                    g.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void setProgressUpdater(@NonNull ProgressUpdater progressUpdater) {

    }
}
