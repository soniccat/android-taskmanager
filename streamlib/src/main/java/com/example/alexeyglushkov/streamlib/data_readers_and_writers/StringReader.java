package com.example.alexeyglushkov.streamlib.data_readers_and_writers;

import com.example.alexeyglushkov.streamlib.convertors.BytesStringConverter;
import com.example.alexeyglushkov.streamlib.handlers.ByteArrayHandler;
import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;
import com.example.alexeyglushkov.streamlib.handlers.StringHandler;

/**
 * Created by alexeyglushkov on 01.02.15.
 */

// TODO: write tests for that
public class StringReader extends ByteArrayReader {

    public StringReader(StringHandler handler) {
        super(createByteArrayHandler(handler), false);
    }

    public StringReader(StringHandler handler, ProgressUpdater progressUpdater) {
        super(createByteArrayHandler(handler), progressUpdater);
    }

    private static ByteArrayHandler createByteArrayHandler(StringHandler handler) {
        return new BytesStringConverter(handler);
    }
}
