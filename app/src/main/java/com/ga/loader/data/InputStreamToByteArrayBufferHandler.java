package com.ga.loader.data;

import com.ga.loader.ProgressUpdater;

import org.apache.http.util.ByteArrayBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by alexeyglushkov on 25.01.15.
 */
public class InputStreamToByteArrayBufferHandler implements InputStreamHandler {

    ByteArrayBufferHandler byteArrayBufferHandler;
    ProgressUpdater progressUpdater;

    public InputStreamToByteArrayBufferHandler(ByteArrayBufferHandler handler) {
        byteArrayBufferHandler = handler;
    }

    public InputStreamToByteArrayBufferHandler(ByteArrayBufferHandler handler, ProgressUpdater progressUpdater) {
        byteArrayBufferHandler = handler;
        this.progressUpdater = progressUpdater;
    }

    @Override
    public Error handleStream(InputStream stream) {
        try {
            ByteArrayBuffer byteArray = this.readStream(stream);
            return byteArrayBufferHandler.handleByteArrayBuffer(byteArray);

        } catch (Exception e) {
            return new Error("handleStream exception: " + e.getMessage());
        }
    }

    public ByteArrayBuffer readStream(InputStream stream) throws IOException {
        ByteArrayBuffer buffer = new ByteArrayBuffer(1024);
        int nRead;
        byte[] data = new byte[1024];

        while ((nRead = stream.read(data, 0, data.length)) != -1) {
            buffer.append(data, 0, nRead);

            if (progressUpdater != null) {
                progressUpdater.append(nRead);
            }
        }

        return buffer;
    }

    public ProgressUpdater getProgressUpdater() {
        return progressUpdater;
    }

    public void setProgressUpdater(ProgressUpdater progressUpdater) {
        this.progressUpdater = progressUpdater;
    }
}
