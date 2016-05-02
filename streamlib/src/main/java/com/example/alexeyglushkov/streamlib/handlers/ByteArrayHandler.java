package com.example.alexeyglushkov.streamlib.handlers;

import com.example.alexeyglushkov.streamlib.convertors.Convertor;

/**
 * Created by alexeyglushkov on 25.01.15.
 */
public interface ByteArrayHandler extends Convertor {
    Object handleByteArrayBuffer(byte[] byteArray);
}
