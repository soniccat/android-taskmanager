package com.example.alexeyglushkov.streamlib.codecs;

import android.support.annotation.Nullable;

import com.example.alexeyglushkov.streamlib.convertors.Converter;
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.ObjectReader;
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.ObjectWriter;

/**
 * Created by alexeyglushkov on 04.10.15.
 */
public class ObjectCodec extends SimpleCodec {
    public ObjectCodec() {
        this(null, null);
    }

    public ObjectCodec(@Nullable Converter writerConverter, @Nullable Converter readerConverter) {
        super(new ObjectWriter(writerConverter), new ObjectReader(readerConverter));
    }
}
