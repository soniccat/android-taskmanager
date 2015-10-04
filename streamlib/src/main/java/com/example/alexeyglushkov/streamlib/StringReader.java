package com.example.alexeyglushkov.streamlib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by alexeyglushkov on 01.02.15.
 */
public class StringReader implements InputStreamReader {
    //TODO: think about cancellation
    private StringHandler stringHandler;
    private Error error;

    public StringReader(StringHandler handler) {
        stringHandler = handler;
    }

    @Override
    public Object readStream(InputStream stream) {
        try {
            String object = this.readStreamToString(stream);
            return stringHandler.handleString(object);

        } catch (Exception e) {
            e.printStackTrace();
            return error = new Error("InputStreamToByteArrayBufferHandler:readStream exception: " + e.getMessage());
        }
    }

    public String readStreamToString(InputStream stream) throws Exception {
        StringBuffer builder = null;
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
                buffreader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String result = null;
        if (builder != null) {
            result = builder.toString();
        }
        return result;
    }

    @Override
    public void setProgressUpdater(ProgressUpdater progressUpdater) {
    }

    @Override
    public Error getError() {
        return error;
    }
}
