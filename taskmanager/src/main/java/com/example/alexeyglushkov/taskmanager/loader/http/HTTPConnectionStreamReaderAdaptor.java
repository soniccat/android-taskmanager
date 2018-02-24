package com.example.alexeyglushkov.taskmanager.loader.http;

import android.support.annotation.NonNull;

import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;
import com.example.alexeyglushkov.streamlib.readersandwriters.InputStreamReader;

import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * Created by alexeyglushkov on 31.10.15.
 */
public class HTTPConnectionStreamReaderAdaptor implements HTTPConnectionStreamReader {
    @NonNull private InputStreamReader reader;

    public HTTPConnectionStreamReaderAdaptor(@NonNull InputStreamReader reader) {
        this.reader = reader;
    }

    @Override
    public void handleConnectionResponse(HttpURLConnection connection) {
    }

    @Override
    public void beginRead(@NonNull InputStream stream) {
        return stream;
    }

    @Override
    public Object read() throws Exception {
        return reader.read();
    }

    @Override
    public void setProgressUpdater(@NonNull ProgressUpdater progressUpdater) {
        reader.setProgressUpdater(progressUpdater);
    }
}
