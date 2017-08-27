package com.example.alexeyglushkov.taskmanager.loader.http;

import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;
import com.example.alexeyglushkov.streamlib.readersandwriters.InputStreamReader;

import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * Created by alexeyglushkov on 31.10.15.
 */
public class HTTPConnectionStreamReaderAdaptor implements HTTPConnectionStreamReader {
    private InputStreamReader reader;

    public HTTPConnectionStreamReaderAdaptor(InputStreamReader reader) {
        this.reader = reader;
    }

    @Override
    public void handleConnectionResponse(HttpURLConnection connection) {
    }

    @Override
    public Object readStream(InputStream data) throws Exception {
        return reader.readStream(data);
    }

    @Override
    public void setProgressUpdater(ProgressUpdater progressUpdater) {
        reader.setProgressUpdater(progressUpdater);
    }
}
