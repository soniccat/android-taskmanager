package com.example.alexeyglushkov.streamlib.data_readers_and_writers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.alexeyglushkov.streamlib.convertors.Converter;
import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;
import com.example.alexeyglushkov.tools.ExceptionTools;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 27.09.15.
 */
public class ByteArrayWriter<T> implements OutputStreamDataWriter<T> {
    private @Nullable OutputStream stream;
    private Converter<T, byte[]> converter;

    public ByteArrayWriter(Converter<T, byte[]> converter) {
        this.converter = converter;
    }

    @Override
    public void beginWrite(@NonNull OutputStream stream) {
        ExceptionTools.throwIfNull(stream, "ByteArrayWriter.beginWrite: stream is null");
        this.stream = new BufferedOutputStream(stream);
    }

    @Override
    public void closeWrite() throws Exception {
        ExceptionTools.throwIfNull(stream, "ByteArrayWriter.closeWrite: stream is null");
        stream.close();
    }

    @Override
    public void write(@NonNull T object) throws Exception {
        ExceptionTools.throwIfNull(stream, "ByteArrayWriter.write: stream is null");

        byte[] buffer = null;
        if (converter != null) {
            buffer = this.converter.convert(object);
        } else {
            buffer = (byte[])object;
        }

        writeByteArray(stream, buffer);
    }

    private void writeByteArray(OutputStream stream, byte[] buffer) throws IOException {
        stream.write(buffer);
    }

    @Override
    public void setProgressUpdater(@NonNull ProgressUpdater progressUpdater) {

    }
}
