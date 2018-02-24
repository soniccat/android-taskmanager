package com.example.alexeyglushkov.streamlib.readersandwriters;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by alexeyglushkov on 17.02.18.
 */

public class InputStreamReaders {

    // call read on a wrapped stream once and closeWrite the wrapped stream
    public static Object readOnce(InputStreamReader reader, InputStream stream) throws Exception {
        Object result = null;
        reader.beginRead(stream);

        try {
            result = reader.read();

        } finally {
            reader.closeRead();
        }

        return result;
    }
}
