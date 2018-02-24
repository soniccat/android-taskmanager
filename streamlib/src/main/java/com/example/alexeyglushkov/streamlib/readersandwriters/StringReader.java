package com.example.alexeyglushkov.streamlib.readersandwriters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;
import com.example.alexeyglushkov.streamlib.handlers.StringHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by alexeyglushkov on 01.02.15.
 */
public class StringReader implements InputStreamReader {
    //TODO: think about cancellation
    java.io.InputStreamReader reader;
    @Nullable private StringHandler stringHandler;

    public StringReader(@Nullable StringHandler handler) {
        stringHandler = handler;
    }

    @Override
    public void beginRead(@NonNull InputStream stream) {
        return stream;
    }

    @Override
    public Object read() throws Exception {
        Object object = this.readStreamToString(stream);
        if (stringHandler != null) {
            object = stringHandler.handleString((String)object);
        }
        return object;
    }

    public String readStreamToString(@NonNull InputStream stream) throws Exception {
        StringBuilder builder = new StringBuilder();
        BufferedReader buffreader = null;
        
        try {
            java.io.InputStreamReader isr = new java.io.InputStreamReader(stream);
            buffreader = new BufferedReader(isr);

            //TODO: we need to read bytes
            String readString = buffreader.readLine();
            while (readString != null) {
                builder.append(readString);
                readString = buffreader.readLine();
            }

        } finally {
            try {
                if (buffreader != null) {
                    buffreader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return builder.toString();
    }

    @Override
    public void setProgressUpdater(@NonNull ProgressUpdater progressUpdater) {
    }
}
