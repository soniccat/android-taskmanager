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

        java.io.OutputStreamWriter writer = null;
        try {
            writer = new java.io.OutputStreamWriter(stream);
            writer.write(string);

        } catch (IOException e) {
            e.printStackTrace();
            return new Error("StringWriter exception: " + e.getMessage());

        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
