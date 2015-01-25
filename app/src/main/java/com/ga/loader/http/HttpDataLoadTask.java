package com.ga.loader.http;

import com.ga.loader.data.ByteArrayBufferHandler;
import com.ga.loader.data.InputStreamToByteArrayBufferHandler;

import org.apache.http.util.ByteArrayBuffer;

import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * Created by alexeyglushkov on 25.01.15.
 */
public class HttpDataLoadTask extends HttpLoadTaskBase {
    protected ByteArrayBuffer data;

    public HttpDataLoadTask(HttpURLConnection connection, final ByteArrayBufferHandler handler) {
        super(connection, null);
        setHandler(new InputStreamToByteArrayBufferHandler(new ByteArrayBufferHandler() {
            @Override
            public Error handleByteArrayBuffer(ByteArrayBuffer byteArray) {
                data = byteArray;
                return handler.handleByteArrayBuffer(data);
            }
        }));
    }

    public ByteArrayBuffer getData() {
        return data;
    }

    protected InputStreamToByteArrayBufferHandler geHandler() {
        return (InputStreamToByteArrayBufferHandler)handler;
    }

    @Override
    protected Error handleStream(InputStream stream) {
        geHandler().setProgressUpdater(createProgressUpdater(contentLength));
        return super.handleStream(stream);
    }
}
