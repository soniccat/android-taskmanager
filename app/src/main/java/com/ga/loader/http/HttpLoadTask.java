package com.ga.loader.http;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

import org.apache.http.util.ByteArrayBuffer;

import com.ga.loader.data.DataHandler;
import com.ga.task.AsyncTask;
import com.ga.task.DataFormat;

public class HttpLoadTask extends AsyncTask {
    protected ByteArrayBuffer data;
    HttpURLConnection connection;
    DataHandler handler;
    int contentLength;

    public HttpLoadTask(HttpURLConnection connection, DataHandler handler) {
        super();
        this.connection = connection;
        this.handler = handler;
        setTaskId(connection.getURL().toString());
    }

    public ByteArrayBuffer getData() {
        return data;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    @Override
    public Void doInBackground(Void... params) {
        try {
            connection.connect();
            int length = connection.getContentLength();
            if (length == -1) {
                length = contentLength;
            } else {
                contentLength = length;
            }

            int response = connection.getResponseCode();
            Log.d("HttpLoadTask","HttpLoadingContext: The response is: " + response + "\n");

            InputStream stream = new BufferedInputStream(connection.getInputStream());
            data = this.readStream(stream, length);

            setTaskError(handler.handleData(data));

        } catch (IOException e) {
            e.printStackTrace();
            setTaskError(new Error("Load error"));

        } finally {
            connection.disconnect();
        }

        handleTaskCompletion();
        return null;
    }

    public ByteArrayBuffer readStream(InputStream stream, int contentSize) throws IOException, UnsupportedEncodingException {
        ByteArrayBuffer buffer = new ByteArrayBuffer(1024);
        int nRead;
        int loadedSize = 0;
        byte[] data = new byte[1024];

        while ((nRead = stream.read(data, 0, data.length)) != -1) {
            buffer.append(data, 0, nRead);
            loadedSize += nRead;

            if (contentSize > 0) {
                float oldPorgress = progress;
                float newProgress = loadedSize / (float)contentSize;

                if (progressMinChange == 0 || newProgress - oldPorgress > progressMinChange) {
                    progress = newProgress;
                    triggerProgressListeners(oldPorgress, progress);
                }
            }
        }

        return buffer;
    }
}
