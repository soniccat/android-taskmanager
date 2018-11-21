package com.example.alexeyglushkov.authtaskmanager;

import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder;
import com.example.alexeyglushkov.service.HttpCacheableTransport;
import com.example.alexeyglushkov.cachemanager.clients.IStorageClient;
import com.example.alexeyglushkov.streamlib.convertors.BytesStringConverter;
import com.example.alexeyglushkov.streamlib.handlers.ByteArrayHandler;
import com.example.alexeyglushkov.taskmanager.loader.http.HTTPConnectionBytesReader;
import com.example.alexeyglushkov.taskmanager.loader.http.TransportTask;
import com.example.alexeyglushkov.taskmanager.loader.http.HttpTaskTransport;
import com.example.alexeyglushkov.taskmanager.loader.http.HttpURLConnectionProvider;
import com.example.alexeyglushkov.taskmanager.task.Task;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by alexeyglushkov on 04.11.15.
 */
public class HttpServiceTask extends TransportTask implements IServiceTask {

    private HttpUrlConnectionBuilder connectionBuilder = new HttpUrlConnectionBuilder();

    public HttpServiceTask() {
        super();
        setTransport(createTransport());
        this.connectionBuilder = new HttpUrlConnectionBuilder();
    }

    private HttpTaskTransport createTransport() {
        final ByteArrayHandler<String> handler = getReader();

        HttpCacheableTransport<String> transport = new HttpCacheableTransport<>(getProvider(), new HTTPConnectionBytesReader<String>() {
            @Override
            public String convert(byte[] object) {
                return handler.convert(object);
            }

            @Override
            public void handleConnectionResponse(HttpURLConnection connection) {
                //
            }
        });

        return transport;
    }

    public void setConnectionBuilder(HttpUrlConnectionBuilder connectionBuilder) {
        this.connectionBuilder = connectionBuilder;
        setTaskId(connectionBuilder.getStringUrl());
        setLoadPolicy(LoadPolicy.CancelAdded);
    }

    public HttpURLConnectionProvider getProvider() {
        return new HttpURLConnectionProvider() {
            @Override
            public HttpURLConnection getUrlConnection() {
                return getConnectionBulder().build();
            }

            @Override
            public URL getURL() {
                URL url = null;
                try {
                    //TODO: return URL from builder
                    url = new URL(connectionBuilder.getStringUrl());
                } catch (MalformedURLException ex) {
                }

                return url;
            }
        };
    }

    protected ByteArrayHandler<String> getReader() {
        return new BytesStringConverter();
    }

    @Override
    public HttpUrlConnectionBuilder getConnectionBulder() {
        return connectionBuilder;
    }

    //TODO: consider to create a servicetaskimpl (subclass of TaskImpl) to remove duplication
    @Override
    public String getResponse() {
        return (String)getHandledData();
    }

    @Override
    public Error getCommandError() {
        return getTaskError();
    }

    @Override
    public boolean isCancelled() {
        return getTaskStatus() == Status.Cancelled;
    }

    @Override
    public int getResponseCode() {
        HttpTaskTransport transport = (HttpTaskTransport) getTransport();
        return transport.getResponseCode();
    }

    public void setCacheClient(IStorageClient cacheClient) {
        HttpCacheableTransport transport = (HttpCacheableTransport) getTransport();
        transport.setCacheClient(cacheClient);
    }
}
