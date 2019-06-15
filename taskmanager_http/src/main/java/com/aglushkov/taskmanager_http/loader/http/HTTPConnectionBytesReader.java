package com.aglushkov.taskmanager_http.loader.http;

import com.example.alexeyglushkov.streamlib.handlers.ByteArrayHandler;

/**
 * Created by alexeyglushkov on 26.11.15.
 */
public interface HTTPConnectionBytesReader<T> extends ByteArrayHandler<T>, HTTPConnectionHandler {
}
