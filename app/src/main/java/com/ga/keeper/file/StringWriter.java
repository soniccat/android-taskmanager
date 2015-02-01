package com.ga.keeper.file;

import org.apache.http.util.ByteArrayBuffer;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 01.02.15.
 */
public class StringWriter implements OutputStreamWriter {

    String string;

    public void StringWriter(String string) {
        this.string = string;
    }

    @Override
    public Error writeToStream(OutputStream stream) {
        try {
            java.io.OutputStreamWriter writer = new java.io.OutputStreamWriter(stream);
            writer.write(string);

        } catch (IOException e) {
            e.printStackTrace();
            return new Error("StringWriter exception: " + e.getMessage());
        }

        return null;
    }
}
