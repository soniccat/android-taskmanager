package com.example.alexeyglushkov.authtaskmanager;

import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder;
import com.aglushkov.taskmanager_http.loader.http.HttpCacheableTransport;
import com.example.alexeyglushkov.cachemanager.clients.Cache;
import com.example.alexeyglushkov.streamlib.handlers.ByteArrayHandler;
import com.aglushkov.taskmanager_http.loader.http.HTTPConnectionBytesReader;
import com.aglushkov.taskmanager_http.loader.http.TransportTask;
import com.aglushkov.taskmanager_http.loader.http.HttpURLConnectionProvider;
import com.example.alexeyglushkov.taskmanager.task.Task;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by alexeyglushkov on 04.11.15.
 */
public class HttpServiceCommand<T> extends BaseServiceTask<T> {
    private HttpUrlConnectionBuilder connectionBuilder;

    public HttpServiceCommand(HttpUrlConnectionBuilder builder, ByteArrayHandler<T> handler) {
        super();
        this.connectionBuilder = builder;
        TransportTask task = new TransportTask();
        setTaskConnectionBuilder(task, builder);
        task.setTransport(new Transport<T>(connectionBuilder, handler));
        setTask(task);
    }

    //// Setters / Getters

    // Setters

    private void setTaskConnectionBuilder(Task task, HttpUrlConnectionBuilder connectionBuilder) {
        task.setTaskId(connectionBuilder.getStringUrl());
        task.setLoadPolicy(Task.LoadPolicy.CancelPreviouslyAdded);
    }

    public void setCacheClient(Cache cacheClient) {
        Transport transport = (Transport) getTransportTask().getTransport();
        transport.setCacheClient(cacheClient);
    }

    // Getters

    @Override
    public HttpUrlConnectionBuilder getConnectionBuilder() {
        return connectionBuilder;
    }

    @Override
    public int getResponseCode() {
        Transport transport = (Transport) getTransportTask().getTransport();
        return transport.getResponseCode();
    }

    private TransportTask getTransportTask() {
        return (TransportTask)getTask();
    }

    //// Classes

    private static class Transport<T> extends HttpCacheableTransport<T> {
        private Transport(HttpUrlConnectionBuilder builder, ByteArrayHandler<T> handler) {
            super(createProvider(builder), createStreamReader(handler));
        }

        static private HttpURLConnectionProvider createProvider(final HttpUrlConnectionBuilder builder) {
            return new HttpURLConnectionProvider() {
                @Override
                public HttpURLConnection getUrlConnection() {
                    return builder.build();
                }

                @Override
                public URL getURL() {
                    return builder.getUrl();
                }
            };
        }

        static private <T>HTTPConnectionBytesReader<T> createStreamReader(final ByteArrayHandler<T> handler) {
            return new HTTPConnectionBytesReader<T>() {
                @Override
                public T convert(byte[] object) {
                    return handler.convert(object);
                }

                @Override
                public void handleConnectionResponse(HttpURLConnection connection) {
                }
            };
        }
    }
}
