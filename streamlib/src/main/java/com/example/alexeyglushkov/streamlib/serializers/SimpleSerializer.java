package com.example.alexeyglushkov.streamlib.serializers;

import com.example.alexeyglushkov.streamlib.readersandwriters.InputStreamReader;
import com.example.alexeyglushkov.streamlib.readersandwriters.OutputStreamWriter;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 04.10.15.
 */
public class SimpleSerializer implements Serializer {
    OutputStreamWriter writer;
    InputStreamReader reader;

    public SimpleSerializer(OutputStreamWriter writer, InputStreamReader reader) {
        this.writer = writer;
        this.reader = reader;
    }

    @Override
    public void setOutputStreamWriter(OutputStreamWriter writer) {
        this.writer = writer;
    }

    @Override
    public void setInputStreamReader(InputStreamReader reader) {
        this.reader = reader;
    }

    @Override
    public Error write(OutputStream outputStream, Object value) {
        return writer.writeStream(outputStream, value);
    }

    @Override
    public Object read(InputStream inputStream) {
        return reader.readStream(inputStream);
    }

    @Override
    public Error getReadError() {
        return reader.getError();
    }

    @Override
    public Error getWriteError() {
        return writer.getError();
    }
}
