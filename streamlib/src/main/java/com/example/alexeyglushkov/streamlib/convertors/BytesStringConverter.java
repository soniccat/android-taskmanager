package com.example.alexeyglushkov.streamlib.convertors;

import com.example.alexeyglushkov.streamlib.handlers.ByteArrayHandler;
import com.example.alexeyglushkov.streamlib.handlers.StringHandler;

import java.nio.charset.Charset;

/**
 * Created by alexeyglushkov on 26.11.15.
 */
public class BytesStringConverter implements Converter, ByteArrayHandler {
    private StringHandler stringHandler;

    public BytesStringConverter(StringHandler stringHandler) {
        this.stringHandler = stringHandler;
    }

    @Override
    public Object convert(Object object) {
        byte[] bytes = (byte[])object;
        return handleByteArrayBuffer(bytes);
    }

    @Override
    public Object handleByteArrayBuffer(byte[] bytes) {
        String string = new String(bytes, 0, bytes.length, Charset.forName("UTF-8"));

        Object resultObject = string;
        if (stringHandler != null) {
            resultObject = stringHandler.handleString((String)resultObject);
        }

        return resultObject;
    }
}
