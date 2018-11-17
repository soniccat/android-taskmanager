package com.example.alexeyglushkov.streamlib.data_readers_and_writers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.alexeyglushkov.streamlib.convertors.Converter;

/**
 * Created by alexeyglushkov on 24.02.18.
 */

public class StringWriter<T> extends ByteArrayWriter<T> {
    public StringWriter(@Nullable final Converter<T, String> converter) {
        super(new Converter<T, byte[]>() {
            @Override
            public byte[] convert(T object) {
                String result = null;
                if (converter != null) {
                    result = converter.convert(object);
                } else {
                    result = (String)object;
                }

                return result.getBytes();
            }
        });
    }
}
