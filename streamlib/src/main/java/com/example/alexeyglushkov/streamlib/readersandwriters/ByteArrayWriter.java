package com.example.alexeyglushkov.streamlib.readersandwriters;

import android.support.annotation.NonNull;

import com.example.alexeyglushkov.streamlib.convertors.Converter;
import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by alexeyglushkov on 27.09.15.
 */
public class ByteArrayWriter implements OutputStreamWriter {
    private Converter converter;

    public ByteArrayWriter(Converter converter) {
        this.converter = converter;
    }

    @Override
    public @NonNull OutputStream wrapStream(OutputStream stream) {
        return stream;
    }

    @Override
    public void write(OutputStream stream, Object object) throws Exception {
        if (converter != null) {
            object = this.converter.convert(object);
        }

        byte[] buffer = (byte[])object;
        writeByteArray(stream, buffer);
    }

    public void writeByteArray(OutputStream stream, byte[] buffer) throws IOException {
        stream.write(buffer);
    }

    @Override
    public void setProgressUpdater(ProgressUpdater progressUpdater) {

    }
}
