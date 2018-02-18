package com.example.alexeyglushkov.streamlib.handlers;

import com.example.alexeyglushkov.streamlib.convertors.Converter;

/**
 * Created by alexeyglushkov on 25.01.15.
 */
public interface ByteArrayHandler extends Converter {
    Object handleByteArrayBuffer(byte[] byteArray);
}
