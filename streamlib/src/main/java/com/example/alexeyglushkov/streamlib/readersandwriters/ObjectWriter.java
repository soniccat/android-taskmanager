package com.example.alexeyglushkov.streamlib.readersandwriters;

import com.example.alexeyglushkov.streamlib.convertors.Convertor;
import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 04.10.15.
 */
public class ObjectWriter implements OutputStreamWriter {
    private Convertor convertor;
    public ObjectWriter(Convertor convertor) {
        this.convertor = convertor;
    }

    @Override
    public void writeStream(OutputStream stream, Object object) throws IOException {
        if (convertor != null) {
            object = convertor.convert(object);
        }

        writeObjectToStream(stream, object);
    }

    public void writeObjectToStream(OutputStream stream, Object object) throws IOException {
        ObjectOutputStream objectStream = null;
        objectStream = new ObjectOutputStream(stream);
        objectStream.writeObject(object);
    }

    @Override
    public void setProgressUpdater(ProgressUpdater progressUpdater) {

    }
}
