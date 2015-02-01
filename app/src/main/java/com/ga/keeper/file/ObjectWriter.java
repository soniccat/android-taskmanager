package com.ga.keeper.file;

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
        try {
            ObjectOutputStream os = new ObjectOutputStream(stream);
			os.writeObject(object);

        } catch (IOException e) {
            e.printStackTrace();
            return new Error("ObjectWriter exception: " + e.getMessage());
        }

        return null;
    }
}
