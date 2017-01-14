package com.example.alexeyglushkov.wordteacher.model;

import com.example.alexeyglushkov.streamlib.readersandwriters.InputStreamReader;
import com.example.alexeyglushkov.streamlib.readersandwriters.OutputStreamWriter;
import com.example.alexeyglushkov.streamlib.serializers.Serializer;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 08.05.16.
 */
public class CourseSerializer implements Serializer {

    private OutputStreamWriter writer = new CourseWriter();
    private InputStreamReader reader = new CourseReader();

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
