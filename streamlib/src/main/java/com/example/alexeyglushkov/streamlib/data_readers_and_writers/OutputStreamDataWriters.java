package com.example.alexeyglushkov.streamlib.data_readers_and_writers;

import androidx.annotation.NonNull;

import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 17.02.18.
 */

public class OutputStreamDataWriters {

    // call write on a wrapped stream once and closeWrite the wrapped stream
    public static <T> void writeOnce(@NonNull OutputStreamDataWriter<T> writer, @NonNull OutputStream stream, @NonNull T object) throws Exception {
        writer.beginWrite(stream);

        try {
            writer.write(object);

        } finally {
            writer.closeWrite();
        }
    }
}
