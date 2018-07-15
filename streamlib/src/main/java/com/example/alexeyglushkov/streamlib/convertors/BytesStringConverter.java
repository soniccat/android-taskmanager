package com.example.alexeyglushkov.streamlib.convertors;

import com.example.alexeyglushkov.streamlib.handlers.ByteArrayHandler;
import com.example.alexeyglushkov.streamlib.handlers.StringHandler;

import java.nio.charset.Charset;

/**
 * Created by alexeyglushkov on 26.11.15.
 */
public class BytesStringConverter implements ByteArrayHandler<String>  {
    public BytesStringConverter() {
    }

    @Override
    public String convert(byte[] bytes) {
        return new String(bytes, 0, bytes.length, Charset.forName("UTF-8"));
    }
}
