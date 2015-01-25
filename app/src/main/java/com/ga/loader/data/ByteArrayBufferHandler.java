package com.ga.loader.data;

import org.apache.http.util.ByteArrayBuffer;

import java.io.InputStream;

/**
 * Created by alexeyglushkov on 25.01.15.
 */
public interface ByteArrayBufferHandler {
    Error handleByteArrayBuffer(ByteArrayBuffer byteArray);
}
