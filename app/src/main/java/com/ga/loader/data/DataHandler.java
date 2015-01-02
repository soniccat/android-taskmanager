package com.ga.loader.data;

import org.apache.http.util.ByteArrayBuffer;

/**
 * Created by alexeyglushkov on 27.12.14.
 */
public interface DataHandler {
    Error handleData(ByteArrayBuffer data);
}
