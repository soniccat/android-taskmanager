package com.example.alexeyglushkov.taskmanager.loader.http;

import android.support.annotation.NonNull;

import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.InputStreamDataReader;

import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * Created by alexeyglushkov on 31.10.15.
 */
public class HTTPConnectionStreamReaderAdaptor implements HTTPConnectionStreamReader {
    @NonNull private InputStreamDataReader reader;

    public HTTPConnectionStreamReaderAdaptor(@NonNull InputStreamDataReader reader) {
        this.reader = reader;
    }

    @Override
    public void handleConnectionResponse(HttpURLConnection connection) {
    }

    @Override
    public void beginRead(@NonNull InputStream stream) throws Exception {
        reader.beginRead(stream);
    }

    @Override
    public void closeRead() throws Exception {
        reader.closeRead();
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
