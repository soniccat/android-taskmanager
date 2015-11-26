package com.example.alexeyglushkov.streamlib.readersandwriters;

import com.example.alexeyglushkov.streamlib.handlers.ByteArrayHandler;
import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;

import org.apache.http.util.ByteArrayBuffer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by alexeyglushkov on 25.01.15.
 */
public class ByteArrayReader implements InputStreamReader {

    //TODO: think about cancellation
    private ByteArrayHandler byteArrayHandler;
    private ProgressUpdater progressUpdater;
    private Error lastError;

    boolean keepByteArray;
    byte[] byteArray;

    public ByteArrayReader(ByteArrayHandler handler, boolean keepByteArray) {
        byteArrayHandler = handler;
        this.keepByteArray = keepByteArray;
    }

    public ByteArrayReader(ByteArrayHandler handler) {
        byteArrayHandler = handler;
    }

    public ByteArrayReader(ByteArrayHandler handler, ProgressUpdater progressUpdater) {
        byteArrayHandler = handler;
        this.progressUpdater = progressUpdater;
    }

    @Override
    public Object readStream(InputStream stream) {
        try {
            byte[] byteArray = this.readStreamToByteArray(stream);
            if (keepByteArray) {
                this.byteArray = byteArray;
            }

            Object result = null;
            if (byteArrayHandler != null) {
                result = byteArrayHandler.handleByteArrayBuffer(byteArray);
            } else {
                result = byteArray;
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return lastError = new Error("InputStreamToByteArrayBufferHandler exception: " + e.getMessage());
        }
    }

    public byte[] readStreamToByteArray(InputStream stream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = stream.read(data, 0, data.length)) != -1) {
            result.write(data);

            if (progressUpdater != null) {
                progressUpdater.append(nRead);
            }
        }

        return result.toByteArray();
    }

    public ByteArrayHandler getByteArrayHandler() {
        return byteArrayHandler;
    }

    public void setByteArrayHandler(ByteArrayHandler byteArrayHandler) {
        this.byteArrayHandler = byteArrayHandler;
    }

    public byte[] getByteArray() {
        return byteArray;
    }

    public ProgressUpdater getProgressUpdater() {
        return progressUpdater;
    }

    public void setProgressUpdater(ProgressUpdater progressUpdater) {
        this.progressUpdater = progressUpdater;
    }

    @Override
    public Error getError() {
        return lastError;
    }
}
