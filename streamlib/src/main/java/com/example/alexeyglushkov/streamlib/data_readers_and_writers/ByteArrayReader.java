package com.example.alexeyglushkov.streamlib.data_readers_and_writers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.alexeyglushkov.streamlib.handlers.ByteArrayHandler;
import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;
import com.example.alexeyglushkov.tools.ExceptionTools;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by alexeyglushkov on 25.01.15.
 */
public class ByteArrayReader<T> implements InputStreamDataReader<T> {
    private @Nullable InputStream stream;
    private ByteArrayHandler<T> byteArrayHandler;
    private ProgressUpdater progressUpdater;

    private boolean keepByteArray;
    private byte[] byteArray;

    public ByteArrayReader(ByteArrayHandler<T> handler, boolean keepByteArray) {
        byteArrayHandler = handler;
        this.keepByteArray = keepByteArray;
    }

    public ByteArrayReader(ByteArrayHandler<T> handler) {
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
    public T read() throws IOException {
        ExceptionTools.throwIfNull(stream, "ByteArrayReader.read: stream is null");

        byte[] byteArray = this.readStreamToByteArray(stream);
        if (keepByteArray) {
            this.byteArray = byteArray;
        }

        T result = null;
        if (byteArrayHandler != null && byteArray != null) {
            result = byteArrayHandler.convert(byteArray);
        } else {
            result = (T)byteArray;
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

    public byte[] getByteArray() {
        return byteArray;
    }

    public void setProgressUpdater(@NonNull  ProgressUpdater progressUpdater) {
        this.progressUpdater = progressUpdater;
    }
}
