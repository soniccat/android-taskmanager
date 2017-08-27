package com.example.alexeyglushkov.taskmanager.loader.http;

import com.example.alexeyglushkov.streamlib.readersandwriters.ByteArrayReader;

import java.net.HttpURLConnection;

/**
 * Created by alexeyglushkov on 26.11.15.
 */
public class HttpBytesLoadTask extends HttpLoadTask {
    protected ByteArrayReader byteArrayReader;

    public HttpBytesLoadTask(HttpURLConnectionProvider provider, final HTTPConnectionBytesReader handler) {
        super(provider, null);

        byteArrayReader = new ByteArrayReader(handler, true);
        setStreamReader(new HTTPConnectionStreamReaderAdaptor(byteArrayReader){
            @Override
            public void handleConnectionResponse(HttpURLConnection connection) {
                if (handler != null) {
                    handler.handleConnectionResponse(connection);
                }
            }
        });
    }
}
