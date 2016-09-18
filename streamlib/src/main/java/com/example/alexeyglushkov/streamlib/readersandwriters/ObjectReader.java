package com.example.alexeyglushkov.streamlib.readersandwriters;

import com.example.alexeyglushkov.streamlib.convertors.Convertor;
import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;

import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * Created by alexeyglushkov on 01.02.15.
 */
public class ObjectReader implements InputStreamReader {
    private Convertor convertor;

    public ObjectReader(Convertor handler) {
        convertor = handler;
    }

    @Override
    public Object readStream(InputStream stream) throws Exception {
        Object result = null;
        Object object = this.readStreamToObject(stream);
        if (convertor != null) {
            result = convertor.convert(object);
        } else {
            result = object;
        }

        return result;
    }

    public Object readStreamToObject(InputStream stream) throws Exception {
        ObjectInputStream objectStream = new ObjectInputStream(stream);
        return objectStream.readObject();
    }

    @Override
    public void setProgressUpdater(ProgressUpdater progressUpdater) {

    }
}
