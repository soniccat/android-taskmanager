package com.example.alexeyglushkov.cachemanager.disk.serializer;

import android.support.annotation.NonNull;

import com.example.alexeyglushkov.cachemanager.disk.DiskStorageMetadata;
import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;
import com.example.alexeyglushkov.streamlib.readersandwriters.OutputStreamWriter;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 11.09.16.
 */
public class DiskMetadataWriter implements OutputStreamWriter {
    @Override
    public @NonNull
    void beginWrite(@NonNull OutputStream stream) {
        return stream;
    }

    @Override
    public void write(@NonNull Object object) throws IOException {
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
