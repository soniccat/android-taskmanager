package com.example.alexeyglushkov.taskmanager.file;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 01.02.15.
 */
public class ByteArrayWriter implements OutputStreamWriter{

    ByteArrayProvider provider;

    public ByteArrayWriter(ByteArrayProvider provider) {
        this.provider = provider;
    }

    @Override
    public Error writeToStream(OutputStream stream) {
        try {
            stream.write(provider.getByteArray().toByteArray());

        } catch (IOException e) {
            e.printStackTrace();
            return new Error("ByteArrayWriter exception: " + e.getMessage());
        }

        return null;
    }
}
