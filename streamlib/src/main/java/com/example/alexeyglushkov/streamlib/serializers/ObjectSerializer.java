package com.example.alexeyglushkov.streamlib.serializers;

import com.example.alexeyglushkov.streamlib.readersandwriters.InputStreamReader;
import com.example.alexeyglushkov.streamlib.readersandwriters.ObjectReader;
import com.example.alexeyglushkov.streamlib.readersandwriters.ObjectWriter;
import com.example.alexeyglushkov.streamlib.readersandwriters.OutputStreamWriter;

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
    public void write(OutputStream outputStream, Object value) throws Exception {
        writer.writeStream(outputStream, value);
    }

    @Override
    public Object read(InputStream inputStream) throws Exception {
        return reader.readStream(inputStream);
    }
}
