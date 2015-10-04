package com.example.alexeyglushkov.streamlib;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 04.10.15.
 */
public class ObjectSerializer implements Serializer {
    private OutputStreamWriter writer = new ObjectWriter(null);
    private InputStreamReader reader = new ObjectReader(null);

    public ObjectSerializer() {
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
