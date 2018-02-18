package com.example.alexeyglushkov.streamlib.readersandwriters;

import android.support.annotation.NonNull;

import com.example.alexeyglushkov.streamlib.handlers.ByteArrayHandler;
import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by alexeyglushkov on 25.01.15.
 */
public class ByteArrayReader implements InputStreamReader {
    private ByteArrayHandler byteArrayHandler;
    private ProgressUpdater progressUpdater;

    private boolean keepByteArray;
    private byte[] byteArray;

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
    @NonNull public InputStream wrapStream(@NonNull InputStream stream) {
        return stream;
    }

    @Override
    public Object read(@NonNull InputStream stream) throws IOException {
        byte[] byteArray = this.readStreamToByteArray(stream);
        if (keepByteArray) {
            this.byteArray = byteArray;
        }

        Object result = null;
        if (byteArrayHandler != null && byteArray != null) {
            result = byteArrayHandler.handleByteArrayBuffer(byteArray);
        } else {
            result = byteArray;
        }

        return result;
    }

    public byte[] readStreamToByteArray(@NonNull InputStream stream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = stream.read(data, 0, data.length)) != -1) {
            result.write(data, 0, nRead);

            if (progressUpdater != null) {
                progressUpdater.append(nRead);

                if (progressUpdater.isCancelled()) {
                    return null;
                }
            }
        }

        if (progressUpdater != null) {
            progressUpdater.finish();
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

    public void setProgressUpdater(@NonNull  ProgressUpdater progressUpdater) {
        this.progressUpdater = progressUpdater;
    }
}
