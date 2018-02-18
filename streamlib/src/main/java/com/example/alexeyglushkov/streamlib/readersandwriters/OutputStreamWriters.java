package com.example.alexeyglushkov.streamlib.readersandwriters;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 17.02.18.
 */

public class OutputStreamWriters {

    // call write on a wrapped stream once and close the wrapped stream
    public static void writeOnce(OutputStreamWriter writer, OutputStream stream, Object object) throws Exception {
        OutputStream wrappedStream = writer.wrapStream(stream);

        try {
            writer.write(wrappedStream, object);

        } finally {
            try {
                wrappedStream.close();
            } catch (IOException e) {
            }
        }
    }
}