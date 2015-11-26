package com.example.alexeyglushkov.taskmanager.loader.http;

import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;
import com.example.alexeyglushkov.streamlib.readersandwriters.InputStreamReader;

import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * Created by alexeyglushkov on 31.10.15.
 */
public class HTTPConnectionResponseReaderAdaptor implements HTTPConnectionStreamReader {
    private InputStreamReader reader;

    public HTTPConnectionResponseReaderAdaptor(InputStreamReader reader) {
        this.reader = reader;
    }

    @Override
    public void handleConnectionResponse(HttpURLConnection connection) {
    }

    @Override
    public Object readStream(InputStream data) {
        return reader.readStream(data);
    }

    @Override
    public void setProgressUpdater(ProgressUpdater progressUpdater) {
        reader.setProgressUpdater(progressUpdater);
    }

    @Override
    public Error getError() {
        return reader.getError();
    }
}
