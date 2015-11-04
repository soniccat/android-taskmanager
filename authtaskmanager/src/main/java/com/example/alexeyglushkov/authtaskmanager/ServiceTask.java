package com.example.alexeyglushkov.authtaskmanager;

import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder;
import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;
import com.example.alexeyglushkov.streamlib.readersandwriters.StringReader;
import com.example.alexeyglushkov.taskmanager.loader.http.HTTPConnectionResponseReader;
import com.example.alexeyglushkov.taskmanager.loader.http.HttpLoadTask;
import com.example.alexeyglushkov.taskmanager.loader.http.HttpURLConnectionProvider;
import com.example.alexeyglushkov.taskmanager.task.SimpleTask;
import com.example.alexeyglushkov.taskmanager.task.Task;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by alexeyglushkov on 04.11.15.
 */
public class ServiceTask extends HttpLoadTask implements ServiceCommand {

    private HttpUrlConnectionBuilder connectionBuilder = new HttpUrlConnectionBuilder();

    public ServiceTask() {
        super(null, null);
        setProvider(getProvider());
        setHandler(getReader());
        this.connectionBuilder = new HttpUrlConnectionBuilder();
    }

    public void setConnectionBuilder(HttpUrlConnectionBuilder connectionBuilder) {
        this.connectionBuilder = connectionBuilder;
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
                    url = new URL(connectionBuilder.getUrl());
                } catch (MalformedURLException ex) {
                }

                return url;
            }
        };
    }

    protected HTTPConnectionResponseReader getReader() {
        return new HTTPConnectionResponseReader() {
            private StringReader stringReader = new StringReader(null);

            @Override
            public void handleConnectionResponse(HttpURLConnection connection) {
            }

            @Override
            public Object readStream(InputStream data) {
                return stringReader.readStream(data);
            }

            @Override
            public void setProgressUpdater(ProgressUpdater progressUpdater) {
                stringReader.setProgressUpdater(progressUpdater);
            }

            @Override
            public Error getError() {
                return stringReader.getError();
            }
        };
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
