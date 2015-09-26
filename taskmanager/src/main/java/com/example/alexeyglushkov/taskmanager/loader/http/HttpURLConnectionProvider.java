package com.example.alexeyglushkov.taskmanager.loader.http;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by alexeyglushkov on 02.08.15.
 */

public interface HttpURLConnectionProvider {
    HttpURLConnection getUrlConnection();
    URL getURL();
}
