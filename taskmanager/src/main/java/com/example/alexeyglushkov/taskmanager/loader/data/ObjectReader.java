package com.example.alexeyglushkov.taskmanager.loader.data;

import com.example.alexeyglushkov.taskmanager.loader.ProgressUpdater;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * Created by alexeyglushkov on 01.02.15.
 */
public class ObjectReader implements InputStreamReader {
    ObjectHandler objectHandler;

    public ObjectReader(ObjectHandler handler) {
        objectHandler = handler;
    }

    @Override
    public Object readStream(InputStream stream) {
        try {
            Object object = this.readStreamToObject(stream);
            Object result;
            if (objectHandler != null) {
                result = objectHandler.handleObject(object);
            } else {
                result = object;
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return new Error("ObjectReader:readStream exception: " + e.getMessage());
        }
    }

    public Object readStreamToObject(InputStream stream) throws Exception {
        Object result = null;
        ObjectInputStream objectStream = null;
        try {
            objectStream = new ObjectInputStream(stream);
            result = objectStream.readObject();

        } finally {
            try {
                objectStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    @Override
    public void setProgressUpdater(ProgressUpdater progressUpdater) {

    }
}
