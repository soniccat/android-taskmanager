package com.example.alexeyglushkov.taskmanager.loader.http;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import com.example.alexeyglushkov.streamlib.readersandwriters.InputStreamReader;
import com.example.alexeyglushkov.streamlib.readersandwriters.StringReader;
import com.example.alexeyglushkov.taskmanager.task.SimpleTask;

// Reader - object which converts a stream to an object of another data type and then delegates it to its handler or just return it if handler is empty
// Handler - object which converts a stream or other input type to an object of another data type and return it, after that it is stored in handledData
// Reader is an extended Handler

public class HttpLoadTask extends SimpleTask {
    protected HttpURLConnectionProvider provider;
    protected int contentLength;
    protected HTTPConnectionStreamReader handler;
    protected Object handledData;
    protected int responseCode;

    public HttpLoadTask(HttpURLConnectionProvider provider, HTTPConnectionStreamReader handler) {
        super();
        setProvider(provider);
        setHandler(handler);
    }

    protected void setProvider(HttpURLConnectionProvider provider) {
        this.provider = provider;

        if (this.provider != null && provider.getURL() != null) {
            setTaskId(provider.getURL().toString());
        }
    }

    protected void setHandler(HTTPConnectionStreamReader handler) {
        this.handler = handler;
    }

    public InputStreamReader getHandler() {
        return handler;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public void startTask() {
        InputStream stream = null;
        HttpURLConnection connection = provider.getUrlConnection();

        try {
            connection.connect();

            int length = connection.getContentLength();
            if (length != -1) {
                setContentLength(length);
            }

            responseCode = connection.getResponseCode();
            Log.d("HttpLoadTask", "HttpLoadingContext: The response is: " + responseCode + "\n");

            handler.setProgressUpdater(getPrivate().createProgressUpdater(contentLength));
            handler.handleConnectionResponse(connection);

            //TODO: handle cancellation well
            stream = new BufferedInputStream(connection.getInputStream());
            Object data = handleStream(stream);
            if (data instanceof Error) {
                setError((Error) data);
            } else {
                setHandledData(data);
            }

        } catch (IOException e) {
            String errorString = getErrorString(connection);
            Error error = new Error(errorString);
            setError(error);
            Log.d("HttpLoadTask", error.toString());

        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException ex) {
            }

            connection.disconnect();
        }

        getPrivate().handleTaskCompletion();
        return;
    }

    @NonNull
    private String getErrorString(HttpURLConnection connection) {
        String result = "HttpLoadTask load error";
        try {
            StringReader errorReader = new StringReader(null);
            String errorString = "";
            InputStream errorStream = connection.getErrorStream();
            if (errorStream != null) {
                errorReader.readStreamToString(errorStream);
            }

            result = "HttpLoadTask load error, url " + connection.getURL() + " code: " + connection.getResponseCode() + " message: " + connection.getResponseMessage() + " response " + errorString;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    protected Object handleStream(InputStream stream) {
        return handler.readStream(stream);
    }

    public Object getHandledData() {
        return handledData;
    }

    public void setError(Error error) {
        getPrivate().setTaskError(error);
    }

    public void setHandledData(Object handledData) {
        this.handledData = handledData;
    }

    public int getResponseCode() {
        return responseCode;
    }
}
