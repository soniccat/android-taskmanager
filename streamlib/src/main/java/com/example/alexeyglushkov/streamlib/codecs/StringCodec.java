package com.example.alexeyglushkov.streamlib.codecs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.alexeyglushkov.streamlib.convertors.Converter;
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.StringReader;
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.StringWriter;
import com.example.alexeyglushkov.streamlib.handlers.StringHandler;

/**
 * Created by alexeyglushkov on 24.02.18.
 */

public class StringCodec<T> extends SimpleCodec<T> {
    public StringCodec(@Nullable Converter<T, String> writerConverter, @Nullable StringHandler<T> readerConverter) {
        super(new StringWriter<T>(writerConverter), new StringReader<T>(readerConverter));
    }
}
