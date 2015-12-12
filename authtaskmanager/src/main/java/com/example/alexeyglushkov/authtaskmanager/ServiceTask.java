package com.example.alexeyglushkov.authtaskmanager;

import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder;
import com.example.alexeyglushkov.cachemanager.CacheProvider;
import com.example.alexeyglushkov.service.CachedHttpLoadTask;
import com.example.alexeyglushkov.streamlib.convertors.BytesStringConvertor;
import com.example.alexeyglushkov.streamlib.handlers.ByteArrayHandler;
import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;
import com.example.alexeyglushkov.streamlib.readersandwriters.StringReader;
import com.example.alexeyglushkov.taskmanager.loader.http.HTTPConnectionBytesReader;
import com.example.alexeyglushkov.taskmanager.loader.http.HTTPConnectionStreamReader;
import com.example.alexeyglushkov.taskmanager.loader.http.HttpURLConnectionProvider;
import com.example.alexeyglushkov.taskmanager.task.Task;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by alexeyglushkov on 04.11.15.
 */
public class ServiceTask extends CachedHttpLoadTask implements ServiceCommand {

    private HttpUrlConnectionBuilder connectionBuilder = new HttpUrlConnectionBuilder();

    public ServiceTask() {
        //TODO: we pass null and lose HTTPConnectionHandler handling
        super(null, null);
        setProvider(getProvider());
        byteArrayReader.setByteArrayHandler(getReader());
        this.connectionBuilder = new HttpUrlConnectionBuilder();
    }

    public void setCache(CacheProvider cache) {
        this.cache = cache;
    }

    public void setConnectionBuilder(HttpUrlConnectionBuilder connectionBuilder) {
        this.connectionBuilder = connectionBuilder;
        setTaskId(connectionBuilder.getStringUrl());
    }

    protected HttpURLConnectionProvider getProvider() {
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
        return new BytesStringConvertor(null);
    }

    @Override
    public HttpUrlConnectionBuilder getConnectionBulder() {
        return connectionBuilder;
    }

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
    public void setServiceCommandCallback(final ServiceCommand.Callback callback) {
        setTaskCallback(new Task.Callback() {
            @Override
            public void onCompleted(boolean cancelled) {
                callback.onCompleted();
            }
        });
    }
}
