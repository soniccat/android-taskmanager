package com.example.alexeyglushkov.streamlib.readersandwriters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 17.02.18.
 */

public class OutputStreamWriters {

    // call write on a wrapped stream once and closeWrite the wrapped stream
    public static void writeOnce(@NonNull OutputStreamWriter writer, @NonNull OutputStream stream, @NonNull Object object) throws Exception {
        writer.beginWrite(stream);

        try {
            writer.write(object);

        } finally {
            writer.closeWrite();
        }
    }
}
