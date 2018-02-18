package com.example.alexeyglushkov.streamlib.readersandwriters;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by alexeyglushkov on 17.02.18.
 */

public class InputStreamReaders {

    // call read on a wrapped stream once and close the wrapped stream
    public static Object readOnce(InputStreamReader reader, InputStream stream) throws Exception {
        Object result = null;
        InputStream wrappedStream = reader.wrapStream(stream);

        try {
            result = reader.read(wrappedStream);

        } finally {
            try {
                wrappedStream.close();
            } catch (IOException e) {
            }
        }

        return result;
    }
}
