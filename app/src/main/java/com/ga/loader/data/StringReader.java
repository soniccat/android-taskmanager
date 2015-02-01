package com.ga.loader.data;

import com.ga.loader.ProgressUpdater;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * Created by alexeyglushkov on 01.02.15.
 */
public class StringReader implements InputStreamReader {
    //TODO: think about cancellation
    StringHandler stringHandler;

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
            return new Error("InputStreamToByteArrayBufferHandler:readStream exception: " + e.getMessage());
        }
    }

    public String readStreamToString(InputStream stream) throws IOException {
        StringBuffer builder = null;
        try {
            java.io.InputStreamReader isr = new java.io.InputStreamReader(stream);
            BufferedReader buffreader = new BufferedReader(isr);

            String readString = buffreader.readLine();
            while (readString != null) {
                builder.append(readString);
                readString = buffreader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
}
