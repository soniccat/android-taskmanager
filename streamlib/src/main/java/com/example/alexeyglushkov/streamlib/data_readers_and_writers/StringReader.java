package com.example.alexeyglushkov.streamlib.data_readers_and_writers;

import androidx.annotation.Nullable;

import com.example.alexeyglushkov.streamlib.convertors.BytesStringConverter;
import com.example.alexeyglushkov.streamlib.handlers.ByteArrayHandler;
import com.example.alexeyglushkov.streamlib.handlers.StringHandler;

/**
 * Created by alexeyglushkov on 01.02.15.
 */

public class StringReader<T> extends ByteArrayReader<T> {

    public StringReader(@Nullable final StringHandler<T> handler) {
        super(new ByteArrayHandler<T>() {
            @Override
            public T convert(byte[] bytes) {
                String string = new BytesStringConverter().convert(bytes);
                T result = null;
                if (handler != null) {
                    result = handler.convert(string);
                } else {
                    result = (T)string;
                }

                return result;
            }
        }, false);
    }
}
