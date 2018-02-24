package com.example.alexeyglushkov.streamlib.readersandwriters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.alexeyglushkov.streamlib.handlers.ByteArrayHandler;
import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;
import com.example.alexeyglushkov.tools.ExceptionTools;

import junit.framework.Assert;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by alexeyglushkov on 25.01.15.
 */
public class ByteArrayReader implements InputStreamReader {
    private @Nullable InputStream stream;
    private ByteArrayHandler byteArrayHandler;
    private ProgressUpdater progressUpdater;

    private boolean keepByteArray;
    private byte[] byteArray;

    public ByteArrayReader(ByteArrayHandler handler, boolean keepByteArray) {
        byteArrayHandler = handler;
        this.keepByteArray = keepByteArray;
    }

    public ByteArrayReader(ByteArrayHandler handler, ProgressUpdater progressUpdater) {
        this(handler);
        this.progressUpdater = progressUpdater;
    }

    public ByteArrayReader(ByteArrayHandler handler) {
        this.byteArrayHandler = handler;
    }

    @Override
    public void beginRead(@NonNull InputStream stream) throws Exception {
        ExceptionTools.throwIfNull(stream, "ByteArrayReader.beginRead: stream is null");
        this.stream = new BufferedInputStream(stream);
    }

    @Override
    public void closeRead() throws Exception {
        ExceptionTools.throwIfNull(stream, "ByteArrayReader.close: stream is null");
        stream.close();
    }

    @Override
    public Object read() throws IOException {
        ExceptionTools.throwIfNull(stream, "ByteArrayReader.read: stream is null");

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

    private byte[] readStreamToByteArray(@NonNull InputStream stream) throws IOException {
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
