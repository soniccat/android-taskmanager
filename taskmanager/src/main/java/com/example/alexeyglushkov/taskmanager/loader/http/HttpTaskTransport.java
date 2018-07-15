package com.example.alexeyglushkov.taskmanager.loader.http;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.InputStreamDataReader;
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.InputStreamDataReaders;
import com.example.alexeyglushkov.streamlib.data_readers_and_writers.StringReader;
import com.example.alexeyglushkov.taskmanager.loader.transport.TaskTransport;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * Created by alexeyglushkov on 28.01.18.
 */

public class HttpTaskTransport<T> implements TaskTransport {
    protected HttpURLConnectionProvider provider;
    protected HTTPConnectionStreamReader<T> streamReader;

    protected int contentLength;
    protected int responseCode;

    private boolean isCancelled;
    private Object data;
    private Error error;

    private ProgressUpdater progressUpdater;

    private Listener listener;

    public HttpTaskTransport(HttpURLConnectionProvider provider, HTTPConnectionStreamReader<T> streamReader) {
        this.provider = provider;
        this.streamReader = streamReader;
    }

    @Override
    public String getId() {
        String id = null;

        if (this.provider != null && provider.getURL() != null) {
            id = (provider.getURL().toString());
        }

        return id;
    }

    @Override
    public void start() {
        HttpURLConnection connection = provider.getUrlConnection();

        try {
            connection.connect();

            int length = connection.getContentLength(); //386020
            if (length != -1) {
                setContentLength(length);
            }

            responseCode = connection.getResponseCode();
            Log.d("HttpLoadTask", "HttpLoadingContext: The response is: " + responseCode + "\n");

            progressUpdater = listener != null ? listener.getProgressUpdater(this, getContentLength()) : null;
            if (progressUpdater != null) {
                streamReader.setProgressUpdater(progressUpdater);
            }

            if (listener == null || listener.needCancel(this)) {
                setIsCancelled();

            } else {
                streamReader.handleConnectionResponse(connection);

                InputStream stream = connection.getInputStream();
                Object data = handleStream(stream);

                if (listener == null || listener.needCancel(this)) {
                    setIsCancelled();

                } else {
                    if (data instanceof Error) {
                        setError((Error) data);
                    } else {
                        setData(data);
                    }
                }
            }

        } catch (Exception e) {
            String errorString = getErrorString(connection);
            Error error = new Error(errorString);
            setError(error);
            Log.d("HttpLoadTask", error.toString());

        } finally {
            connection.disconnect();
        }
    }

    protected Object handleStream(InputStream stream) throws Exception {
        return InputStreamDataReaders.readOnce(streamReader, stream);
    }

    @Override
    public void cancel() {
        progressUpdater = null;
    }

    //// Setters / Getters

    // Setters

    private void setIsCancelled() {
        isCancelled = true;
    }

    protected void setStreamReader(HTTPConnectionStreamReader<T> handler) {
        this.streamReader = handler;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    @Override
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public void setData(Object data) {
        this.data = data;
    }

    // Getters


    @Override
    public ProgressUpdater getProgressUpdater() {
        return progressUpdater;
    }

    public InputStreamDataReader getStreamReader() {
        return streamReader;
    }

    public int getContentLength() {
        return contentLength;
    }

    @Override
    public Error getError() {
        return error;
    }

    @Override
    public Object getData() {
        return data;
    }

    public int getResponseCode() {
        return responseCode;
    }

    @NonNull
    private String getErrorString(HttpURLConnection connection) {
        String result = "HttpLoadTask load error";
        try {
            StringReader<String> errorReader = new StringReader<String>(null);
            String errorString = "";
            InputStream errorStream = connection.getErrorStream();
            if (errorStream != null) {
                errorString = InputStreamDataReaders.readOnce(errorReader, errorStream);
            }

            result = "HttpLoadTask load error, url " + connection.getURL() + " code: " + connection.getResponseCode() + " message: " + connection.getResponseMessage() + " response " + errorString;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public Listener getListener() {
        return listener;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }
}
