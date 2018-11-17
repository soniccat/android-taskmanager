package com.example.alexeyglushkov.streamlib.data_readers_and_writers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.alexeyglushkov.streamlib.convertors.Converter;
import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;
import com.example.alexeyglushkov.tools.ExceptionTools;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * Created by alexeyglushkov on 01.02.15.
 */
public class ObjectReader<T> implements InputStreamDataReader<T> {
    @Nullable private ObjectInputStream stream;
    @Nullable private Converter<Object, T> converter;

    public ObjectReader(@Nullable Converter<Object, T> handler) {
        converter = handler;
    }

    @Override
    public void beginRead(@NonNull InputStream stream) throws Exception {
        ExceptionTools.throwIfNull(stream, "ObjectReader.beginRead: stream is null");
        this.stream = new ObjectInputStream(new BufferedInputStream(stream));
    }

    @Override
    public void closeRead() throws Exception {
        ExceptionTools.throwIfNull(stream, "ObjectReader.close: stream is null");
        this.stream.close();
    }

    @Override
    public @Nullable T read() throws Exception {
        ExceptionTools.throwIfNull(stream, "ObjectReader.read: stream is null");

        T result = null;
        Object object = this.readStreamToObject(stream);
        if (converter != null) {
            result = converter.convert(object);
        } else {
            result = (T)object;
        }

        return result;
    }

    public @Nullable Object readStreamToObject(@NonNull ObjectInputStream stream) throws Exception {
        return stream.readObject();
    }

    @Override
    public void setProgressUpdater(@NonNull ProgressUpdater progressUpdater) {

    }
}
