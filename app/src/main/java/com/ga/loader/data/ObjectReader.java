package com.ga.loader.data;

import com.ga.loader.ProgressUpdater;

import org.apache.http.util.ByteArrayBuffer;

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
            return objectHandler.handleObject(object);

        } catch (Exception e) {
            e.printStackTrace();
            return new Error("InputStreamToByteArrayBufferHandler:readStream exception: " + e.getMessage());
        }
    }

    public Object readStreamToObject(InputStream stream) throws IOException {
        Object result = null;
        try {
            ObjectInputStream is = new ObjectInputStream(stream);
            result = is.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public void setProgressUpdater(ProgressUpdater progressUpdater) {

    }
}
