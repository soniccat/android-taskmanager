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
    private Error lastError;

    public ObjectReader(Convertor handler) {
        convertor = handler;
    }

    @Override
    public Object readStream(InputStream stream) {
        lastError = null;

        Object result = null;
        try {
            Object object = this.readStreamToObject(stream);
            if (convertor != null) {
                result = convertor.convert(object);
            } else {
                result = object;
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            lastError = new Error("ObjectReader:readStream exception: " + e.getMessage());
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

    @Override
    public Error getError() {
        return lastError;
    }
}
