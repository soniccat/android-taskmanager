package com.taskmanager.keeper.file;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 01.02.15.
 */
public class ObjectWriter implements OutputStreamWriter {
    Object object;

    public ObjectWriter(Object object) {
        this.object = object;
    }

    @Override
    public Error writeToStream(OutputStream stream) {

        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(stream);
            objectOutputStream.writeObject(object);

        } catch (IOException e) {
            e.printStackTrace();
            return new Error("ObjectWriter exception: " + e.getMessage());

        } finally {
            try {
                objectOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
