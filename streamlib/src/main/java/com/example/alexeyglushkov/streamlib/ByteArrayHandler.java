package com.example.alexeyglushkov.streamlib;

import org.apache.http.util.ByteArrayBuffer;

/**
 * Created by alexeyglushkov on 25.01.15.
 */
public interface ByteArrayHandler extends Convertor {
    Object handleByteArrayBuffer(ByteArrayBuffer byteArray);
}
