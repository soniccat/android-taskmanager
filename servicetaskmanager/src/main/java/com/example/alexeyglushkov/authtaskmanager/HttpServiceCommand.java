package com.example.alexeyglushkov.authtaskmanager;

import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder;
import com.example.alexeyglushkov.service.HttpCacheableTransport;
import com.example.alexeyglushkov.cachemanager.clients.Cache;
import com.example.alexeyglushkov.streamlib.handlers.ByteArrayHandler;
import com.example.alexeyglushkov.taskmanager.loader.http.HTTPConnectionBytesReader;
import com.example.alexeyglushkov.taskmanager.loader.http.TransportTask;
import com.example.alexeyglushkov.taskmanager.loader.http.HttpURLConnectionProvider;
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
        TransportTask task = new TransportTask();
        setConnectionBuilder(builder);
        task.setTransport(new Transport<T>(connectionBuilder, handler));
        setTask(task);
    }

    //// Setters / Getters

    // Setters

    private void setConnectionBuilder(HttpUrlConnectionBuilder connectionBuilder) {
        this.connectionBuilder = connectionBuilder;
        task.setTaskId(connectionBuilder.getStringUrl());
        task.setLoadPolicy(Task.LoadPolicy.CancelAdded);
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
