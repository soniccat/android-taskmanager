package com.example.alexeyglushkov.streamlib.readersandwriters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.alexeyglushkov.streamlib.convertors.Converter;
import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;

import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * Created by alexeyglushkov on 01.02.15.
 */
public class ObjectReader implements InputStreamReader {
    @Nullable private Converter converter;

    public ObjectReader(@Nullable Converter handler) {
        converter = handler;
    }

    @Override
    public InputStream wrapStream(@NonNull InputStream stream) {
        return null;
    }

    @Override
    public @Nullable Object read(@NonNull InputStream stream) throws Exception {
        Object result = null;
        Object object = this.readStreamToObject(stream);
        if (converter != null) {
            result = converter.convert(object);
        } else {
            result = object;
        }

        return result;
    }

    public @Nullable Object readStreamToObject(@NonNull InputStream stream) throws Exception {
        ObjectInputStream objectStream = new ObjectInputStream(stream);
        return objectStream.readObject();
    }

    @Override
    public void setProgressUpdater(@NonNull ProgressUpdater progressUpdater) {

    }
}
