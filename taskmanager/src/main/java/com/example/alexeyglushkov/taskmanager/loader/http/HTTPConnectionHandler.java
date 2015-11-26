package com.example.alexeyglushkov.taskmanager.loader.http;

import java.net.HttpURLConnection;

/**
 * Created by alexeyglushkov on 26.11.15.
 */
public interface HTTPConnectionHandler {
    void handleConnectionResponse(HttpURLConnection connection);
}
