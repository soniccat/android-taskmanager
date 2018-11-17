package com.example.alexeyglushkov.streamlib.codecs;

import androidx.annotation.Nullable;

import com.example.alexeyglushkov.streamlib.convertors.Converter;
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.ObjectReader;
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.ObjectWriter;

/**
 * Created by alexeyglushkov on 04.10.15.
 */
public class ObjectCodec<T> extends SimpleCodec<T> {
    public ObjectCodec() {
        this(null, null);
    }

    public ObjectCodec(@Nullable Converter<T, Object> writerConverter, @Nullable Converter<Object, T> readerConverter) {
        super(new ObjectWriter<T>(writerConverter), new ObjectReader<T>(readerConverter));
    }
}
