package com.example.alexeyglushkov.streamlib.serializers;

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
    public void setOutputStreamWriter(@NonNull OutputStreamWriter writer) {
        this.writer = writer;
    }

    @Override
    public void setInputStreamReader(@NonNull InputStreamReader reader) {
        this.reader = reader;
    }

    @Override
    public InputStream wrapInputStream(@NonNull InputStream stream) throws Exception {
        return reader.wrapInputStream(stream);
    }

    @Override
    public OutputStream wrapOutputStream(@NonNull OutputStream stream) throws Exception {
        return writer.wrapOutputStream(stream);
    }

    @Override
    public void write(@NonNull OutputStream outputStream, @NonNull Object value) throws Exception {
        writer.writeStream(outputStream, value);
    }

    @Override
    public @Nullable Object read(@NonNull InputStream inputStream) throws Exception {
        return reader.readStream(inputStream);
    }
}
