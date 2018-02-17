package com.example.alexeyglushkov.taskmanager.loader.http;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;
import com.example.alexeyglushkov.streamlib.readersandwriters.InputStreamReader;
import com.example.alexeyglushkov.streamlib.readersandwriters.StringReader;
import com.example.alexeyglushkov.taskmanager.loader.transport.TaskTransport;
import com.example.alexeyglushkov.taskmanager.task.SimpleTask;

// Reader - object which converts a stream to an object of another data type and then delegates it to its streamReader or just return it if streamReader is empty
// Handler - object which converts a stream or other input type to an object of another data type and return it, after that it is stored in handledData
// Reader is an extended Handler

public class TransportTask extends SimpleTask implements TaskTransport.Listener {
    protected Object handledData; // TODO: use result object and api

    private TaskTransport transport;
    protected ProgressUpdater progressUpdater;

    protected TransportTask() {
        super();
    }

    public TransportTask(TaskTransport transport) {
        super();
        setTransport(transport);
    }

    protected void setTransport(@NonNull TaskTransport transport) {
        TaskTransport oldTransport = getTransport();
        if (oldTransport != null && oldTransport.getListener() == this) {
            oldTransport.setListener(null);
        }

        this.transport = transport;
        transport.setListener(this);

        String transportId = transport.getId();
        if (transportId != null) {
            setTaskId(transportId);
        }
    }

    public TaskTransport getTransport() {
        return transport;
    }

    public void startTask(Callback callback) {
        super.startTask(callback);

        transport.start();

        if (needCancelTask) {
            setIsCancelled();

        } else {
            Error e = transport.getError();
            Object d = transport.getData();

            if (e != null) {
                setError(e);
            } else {
                setHandledData(d);
            }
        }

        getPrivate().handleTaskCompletion(callback);
    }

    public Object getHandledData() {
        return handledData;
    }

    public void setError(Error error) {
        getPrivate().setTaskError(error);
    }

    public void setHandledData(Object handledData) {
        this.handledData = handledData;
    }

    @Override
    public boolean canBeCancelledImmediately() {
        return true;
    }

    @Override
    public void clear() {
        super.clear();

        transport.setListener(null);
        transport = null;
        handledData = null;
        progressUpdater = null;
    }

    @Override
    public void cancelTask(Object info) {
        if (progressUpdater != null) {
            synchronized (this) {
                ProgressUpdater updater = progressUpdater;
                progressUpdater = null;

                updater.cancel(info); // that calls this methods again
            }
        } else {
            transport.cancel();
            super.cancelTask(info);
        }
    }

    // TaskTransport.Listener

    @Override
    public ProgressUpdater getProgressUpdater(TaskTransport transport, float size) {
        return createProgressUpdater(size);
    }

    @Override
    public boolean needCancel(TaskTransport transport) {
        return getNeedCancelTask();
    }
}
