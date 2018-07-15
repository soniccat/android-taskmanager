package com.example.alexeyglushkov.streamlib.data_readers_and_writers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.alexeyglushkov.streamlib.convertors.Converter;
import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;
import com.example.alexeyglushkov.tools.ExceptionTools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 04.10.15.
 */
public class ObjectWriter<T> implements OutputStreamDataWriter<T> {
    @Nullable private ObjectOutputStream stream;
    @Nullable private Converter<T, Object> converter;

    public ObjectWriter(@Nullable Converter<T, Object> converter) {
        this.converter = converter;
    }

    @Override
    public void beginWrite(@NonNull OutputStream stream) throws IOException {
        ExceptionTools.throwIfNull(stream, "ObjectWriter.beginWrite: stream is null");
        this.stream = new ObjectOutputStream(new BufferedOutputStream(stream));
    }

    @Override
    public void closeWrite() throws Exception {
        ExceptionTools.throwIfNull(stream, "ObjectWriter.closeWrite: stream is null");
        stream.close();
    }

    @Override
    public void write(@NonNull T object) throws IOException {
        ExceptionTools.throwIfNull(stream, "ObjectWriter.write: stream is null");

        Object toWriteObject = null;
        if (converter != null) {
            toWriteObject = converter.convert(object);
        } else {
            toWriteObject = object;
        }

        writeObjectToStream(stream, toWriteObject);
    }

    private void writeObjectToStream(@NonNull ObjectOutputStream stream, @NonNull Object object) throws IOException {
        stream.writeObject(object);
    }

    @Override
    public void setProgressUpdater(@NonNull ProgressUpdater progressUpdater) {

    }
}
