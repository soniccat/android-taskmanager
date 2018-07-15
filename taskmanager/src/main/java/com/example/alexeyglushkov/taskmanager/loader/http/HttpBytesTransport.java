package com.example.alexeyglushkov.taskmanager.loader.http;

import com.example.alexeyglushkov.streamlib.data_readers_and_writers.ByteArrayReader;

import java.net.HttpURLConnection;

/**
 * Created by alexeyglushkov on 26.11.15.
 */
public class HttpBytesTransport<T> extends HttpTaskTransport<T> {
    protected ByteArrayReader<T> byteArrayReader;

    public HttpBytesTransport(HttpURLConnectionProvider provider, final HTTPConnectionBytesReader<T> handler) {
        super(provider, null);

        byteArrayReader = new ByteArrayReader<>(handler, true);
        setStreamReader(new HTTPConnectionStreamReaderAdaptor<T>(byteArrayReader){
            @Override
            public void handleConnectionResponse(HttpURLConnection connection) {
                if (handler != null) {
                    handler.handleConnectionResponse(connection);
                }
            }
        });
    }
}
