package com.example.alexeyglushkov.streamlib.serializers;

import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;
import com.example.alexeyglushkov.streamlib.readersandwriters.InputStreamReader;
import com.example.alexeyglushkov.streamlib.readersandwriters.OutputStreamWriter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 04.10.15.
 */
public class SimpleSerializer implements Serializer {
    @NonNull private OutputStreamWriter writer;
    @NonNull private InputStreamReader reader;

    public SimpleSerializer(@NonNull OutputStreamWriter writer, @NonNull InputStreamReader reader) {
        this.writer = writer;
        this.reader = reader;
    }

    @Override
    public @NonNull
    void beginRead(@NonNull InputStream stream) throws Exception {
        return reader.beginRead(stream);
    }

    @Override
    public @NonNull
    void beginWrite(@NonNull OutputStream stream) throws Exception {
        return writer.beginWrite(stream);
    }

    @Override
    public void write(@NonNull Object value) throws Exception {
        writer.write(value);
    }

    @Override
    public @Nullable Object read() throws Exception {
        return reader.read();
    }

    @Override
    public void closeWrite() throws Exception {
        writer.closeWrite();
    }

    @Override
    public void closeRead() throws Exception {
        reader.closeRead();
    }

    @Override
    public void setProgressUpdater(@NonNull ProgressUpdater progressUpdater) {
        reader.setProgressUpdater(progressUpdater);
        writer.setProgressUpdater(progressUpdater);
    }
}
