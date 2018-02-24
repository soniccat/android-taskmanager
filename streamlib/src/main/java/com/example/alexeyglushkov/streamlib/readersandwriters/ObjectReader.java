package com.example.alexeyglushkov.streamlib.readersandwriters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.alexeyglushkov.streamlib.convertors.Converter;
import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;
import com.example.alexeyglushkov.tools.ExceptionTools;

import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * Created by alexeyglushkov on 01.02.15.
 */
public class ObjectReader implements InputStreamReader {
    @Nullable private ObjectInputStream stream;
    @Nullable private Converter converter;

    public ObjectReader(@Nullable Converter handler) {
        converter = handler;
    }

    @Override
    public void beginRead(@NonNull InputStream stream) throws Exception {
        ExceptionTools.throwIfNull(stream, "ObjectReader.beginRead: stream is null");
        this.stream = new ObjectInputStream(stream);
    }

    @Override
    public void closeRead() throws Exception {
        ExceptionTools.throwIfNull(stream, "ObjectReader.close: stream is null");
        this.stream.close();
    }

    @Override
    public @Nullable Object read() throws Exception {
        ExceptionTools.throwIfNull(stream, "ObjectReader.read: stream is null");

        Object result = null;
        Object object = this.readStreamToObject(stream);
        if (converter != null) {
            result = converter.convert(object);
        } else {
            result = object;
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
