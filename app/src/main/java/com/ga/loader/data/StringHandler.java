package com.ga.loader.data;

import org.apache.http.util.ByteArrayBuffer;

/**
 * Created by alexeyglushkov on 28.12.14.
 */
public interface StringHandler {
    Error handleString(String data);
}
