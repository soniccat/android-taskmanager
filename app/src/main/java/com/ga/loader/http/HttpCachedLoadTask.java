package com.ga.loader.http;

import com.ga.loader.data.DataHandler;
import com.ga.task.DataFormat;

import java.net.HttpURLConnection;

public class HttpCachedLoadTask extends HttpLoadTask {

    public HttpCachedLoadTask(HttpURLConnection connection, DataHandler handler) {
        super(connection, handler);
    }

}
