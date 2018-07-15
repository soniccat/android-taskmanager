package com.example.alexeyglushkov.streamlib.data_readers_and_writers;

import java.io.InputStream;

/**
 * Created by alexeyglushkov on 17.02.18.
 */

public class InputStreamDataReaders {

    // call read on a wrapped stream once and closeWrite the wrapped stream
    public static <T> T readOnce(InputStreamDataReader<T> reader, InputStream stream) throws Exception {
        T result = null;
        reader.beginRead(stream);

        try {
            result = reader.read();

        } finally {
            reader.closeRead();
        }

        return result;
    }
}
