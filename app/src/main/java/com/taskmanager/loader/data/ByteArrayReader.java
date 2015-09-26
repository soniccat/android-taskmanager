package com.taskmanager.loader.data;

import com.taskmanager.loader.ProgressUpdater;

import org.apache.http.util.ByteArrayBuffer;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by alexeyglushkov on 25.01.15.
 */
public class ByteArrayReader implements InputStreamReader {

    //TODO: think about cancellation
    ByteArrayHandler byteArrayHandler;
    ProgressUpdater progressUpdater;

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
            ByteArrayBuffer byteArray = this.readStreamToByteArray(stream);
            Object result = null;
            if (byteArrayHandler != null) {
                result = byteArrayHandler.handleByteArrayBuffer(byteArray);
            } else {
                result = byteArray;
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return new Error("InputStreamToByteArrayBufferHandler exception: " + e.getMessage());
        }
    }

    public ByteArrayBuffer readStreamToByteArray(InputStream stream) throws IOException {
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
