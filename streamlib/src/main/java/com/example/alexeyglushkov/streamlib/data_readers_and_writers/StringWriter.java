package com.example.alexeyglushkov.streamlib.data_readers_and_writers;

import android.support.annotation.NonNull;

import com.example.alexeyglushkov.streamlib.convertors.Converter;

/**
 * Created by alexeyglushkov on 24.02.18.
 */

public class StringWriter extends ByteArrayWriter {
    public StringWriter(final Converter converter) {
        super(new Converter() {
            @Override
            public Object convert(Object object) {
                if (converter != null) {
                    object = converter.convert(object);
                }

                String str = (String)object;
                return str.getBytes();
            }
        });
    }
}
