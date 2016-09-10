package com.example.alexeyglushkov.streamlib.readersandwriters;

import com.example.alexeyglushkov.streamlib.convertors.Convertor;
import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;

import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 04.10.15.
 */
public class ObjectWriter implements OutputStreamWriter {
    private Convertor convertor;
    private Error lastError;

    public ObjectWriter(Convertor convertor) {
        this.convertor = convertor;
    }

    @Override
    public Error writeStream(OutputStream stream, Object object) {
        lastError = null;

        if (convertor != null) {
            object = convertor.convert(object);
        }

        lastError = writeObjectToStream(stream, object);
        return lastError;
    }

    public Error writeObjectToStream(OutputStream stream, Object object) {
        ObjectOutputStream objectStream = null;
        try {
            objectStream = new ObjectOutputStream(stream);
            objectStream.writeObject(object);
        } catch (Exception ex) {
            return new Error("ObjectWriter writeObjectToStream open stream or writer exception: " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setProgressUpdater(ProgressUpdater progressUpdater) {

    }

    @Override
    public Error getError() {
        return lastError;
    }
}
