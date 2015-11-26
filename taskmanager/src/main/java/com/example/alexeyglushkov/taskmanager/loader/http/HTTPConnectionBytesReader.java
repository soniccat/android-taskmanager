package com.example.alexeyglushkov.taskmanager.loader.http;

import com.example.alexeyglushkov.streamlib.handlers.ByteArrayHandler;
import com.example.alexeyglushkov.streamlib.readersandwriters.InputStreamReader;

import java.net.HttpURLConnection;

/**
 * Created by alexeyglushkov on 26.11.15.
 */
public interface HTTPConnectionBytesReader extends ByteArrayHandler, HTTPConnectionHandler {
}
