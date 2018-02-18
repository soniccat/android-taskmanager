package com.example.alexeyglushkov.authtaskmanager;

import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder;
import com.example.alexeyglushkov.cachemanager.StorageProvider;
import com.example.alexeyglushkov.service.HttpCacheableTransport;
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
        //TODO: we pass null and lose HTTPConnectionHandler handling (HTTPConnectionResponseReaderAdaptor)
        //super(null, null);
        super();
        setTransport(createTransport());
        //setProvider(getProvider());
        //byteArrayReader.setByteArrayHandler(getReader());
        this.connectionBuilder = new HttpUrlConnectionBuilder();
    }

    private HttpTaskTransport createTransport() {
        final ByteArrayHandler handler = getReader();

        HttpCacheableTransport transport = new HttpCacheableTransport(getProvider(), new HTTPConnectionBytesReader() {
            @Override
            public Object convert(Object object) {
                return handleByteArrayBuffer((byte[])object);
            }

            @Override
            public Object handleByteArrayBuffer(byte[] byteArray) {
                return handler.handleByteArrayBuffer(byteArray);
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

    protected ByteArrayHandler getReader() {
        return new BytesStringConverter(null);
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
    public void setServiceCommandCallback(final ServiceCommand.CommandCallback callback) {
        setTaskCallback(new Task.Callback() {
            @Override
            public void onCompleted(boolean cancelled) {
                callback.onCompleted(getCommandError());
            }
        });
    }

    @Override
    public ServiceCommand getServiceCommand() {
        return this;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int getResponseCode() {
        HttpTaskTransport transport = (HttpTaskTransport) getTransport();
        return transport.getResponseCode();
    }

    public void setCache(StorageProvider cache) {
        HttpCacheableTransport transport = (HttpCacheableTransport) getTransport();
        transport.setCache(cache);
    }
}
