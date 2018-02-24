package com.example.alexeyglushkov.streamlib.data_readers_and_writers;

import android.support.annotation.NonNull;

import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 17.02.18.
 */

public class OutputStreamDataWriters {

    // call write on a wrapped stream once and closeWrite the wrapped stream
    public static void writeOnce(@NonNull OutputStreamDataWriter writer, @NonNull OutputStream stream, @NonNull Object object) throws Exception {
        writer.beginWrite(stream);

        try {
            writer.write(object);

        } finally {
            writer.closeWrite();
        }
    }
}
