package com.example.alexeyglushkov.streamlib.readersandwriters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.alexeyglushkov.streamlib.convertors.Converter;
import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;

import junit.framework.Assert;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 04.10.15.
 */
public class ObjectWriter implements OutputStreamWriter {
    @Nullable private Converter converter;

    public ObjectWriter(@Nullable Converter converter) {
        this.converter = converter;
    }

    @Override
    public @NonNull OutputStream wrapOutputStream(@NonNull OutputStream stream) throws IOException {
        return new ObjectOutputStream(stream);
    }

    @Override
    public void writeStream(@NonNull OutputStream stream, @NonNull Object object) throws IOException {
        if (converter != null) {
            object = converter.convert(object);
        }

        writeObjectToStream(stream, object);
    }

    public void writeObjectToStream(@NonNull OutputStream stream, @NonNull Object object) throws IOException {
        Assert.assertTrue("wrapOutputStream wasn't called", stream instanceof ObjectOutputStream);

        ObjectOutputStream objectStream = (ObjectOutputStream)stream;
        objectStream.writeObject(object);
    }

    @Override
    public void setProgressUpdater(@NonNull ProgressUpdater progressUpdater) {

    }
}
