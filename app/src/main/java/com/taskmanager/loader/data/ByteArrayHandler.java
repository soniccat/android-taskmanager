package com.taskmanager.loader.data;

import org.apache.http.util.ByteArrayBuffer;

/**
 * Created by alexeyglushkov on 25.01.15.
 */
public interface ByteArrayHandler {
    Object handleByteArrayBuffer(ByteArrayBuffer byteArray);
}
