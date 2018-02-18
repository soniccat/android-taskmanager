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
    public @NonNull InputStream wrapStream(@NonNull InputStream stream) throws Exception {
        return reader.wrapStream(stream);
    }

    @Override
    public @NonNull OutputStream wrapStream(@NonNull OutputStream stream) throws Exception {
        return writer.wrapStream(stream);
    }

    @Override
    public void write(@NonNull OutputStream outputStream, @NonNull Object value) throws Exception {
        writer.write(outputStream, value);
    }

    @Override
    public @Nullable Object read(@NonNull InputStream inputStream) throws Exception {
        return reader.read(inputStream);
    }

    @Override
    public void setProgressUpdater(@NonNull ProgressUpdater progressUpdater) {
        reader.setProgressUpdater(progressUpdater);
        writer.setProgressUpdater(progressUpdater);
    }
}
