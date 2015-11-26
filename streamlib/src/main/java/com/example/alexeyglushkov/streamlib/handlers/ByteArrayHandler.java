package com.example.alexeyglushkov.streamlib.handlers;

import com.example.alexeyglushkov.streamlib.convertors.Convertor;

import org.apache.http.util.ByteArrayBuffer;

/**
 * Created by alexeyglushkov on 25.01.15.
 */
public interface ByteArrayHandler extends Convertor {
    Object handleByteArrayBuffer(byte[] byteArray);
}
