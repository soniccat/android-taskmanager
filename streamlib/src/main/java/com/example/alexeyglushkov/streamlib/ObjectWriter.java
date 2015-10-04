package com.example.alexeyglushkov.streamlib;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
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
    public Error writeStream(OutputStream stream, Object object) {
        if (convertor != null) {
            object = convertor.convert(object);
        }

        return writeObjectToStream(stream, object);
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
        return null;
    }
}
