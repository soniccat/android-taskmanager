package com.ga.loader.http;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import com.ga.loader.ProgressUpdater;
import com.ga.loader.data.InputStreamHandler;
import com.ga.task.AsyncTask;

public class HttpLoadTaskBase extends AsyncTask {
    protected HttpURLConnection connection;
    protected int contentLength;
    protected InputStreamHandler handler;

    public HttpLoadTaskBase(HttpURLConnection connection, InputStreamHandler handler) {
        super();
        this.connection = connection;
        this.handler = handler;
        setTaskId(connection.getURL().toString());
    }

    public void setHandler(InputStreamHandler handler) {
        this.handler = handler;
    }

    public InputStreamHandler getHandler() {
        return handler;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    @Override
    public Void doInBackground(Void... params) {
        InputStream stream = null;

        try {
            connection.connect();
            int length = connection.getContentLength();
            if (length != -1) {
                setContentLength(length);
            }

            int response = connection.getResponseCode();
            Log.d("HttpLoadTask","HttpLoadingContext: The response is: " + response + "\n");

            stream = new BufferedInputStream(connection.getInputStream());
            setTaskError(handleStream(stream));

        } catch (IOException e) {
            e.printStackTrace();
            setTaskError(new Error("Load error"));

        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException ex) {
            }

            connection.disconnect();
        }

        handleTaskCompletion();
        return null;
    }

    protected Error handleStream(InputStream stream) {
        return handler.handleStream(stream);
    }
}
