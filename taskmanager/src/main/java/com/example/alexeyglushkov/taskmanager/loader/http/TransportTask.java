package com.example.alexeyglushkov.taskmanager.loader.http;

import androidx.annotation.NonNull;

import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;
import com.example.alexeyglushkov.taskmanager.loader.transport.TaskTransport;
import com.example.alexeyglushkov.taskmanager.task.SimpleTask;

public class TransportTask extends SimpleTask implements TaskTransport.Listener {
    private TaskTransport transport;

    public TransportTask() {
        super();
    }

    public TransportTask(TaskTransport transport) {
        super();
        setTransport(transport);
    }

    public void setTransport(@NonNull TaskTransport transport) {
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
                setTaskResult(d);
            }
        }

        getPrivate().handleTaskCompletion(callback);
    }

    public void setError(Error error) {
        getPrivate().setTaskError(error);
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
    }

    @Override
    public void cancelTask(Object info) {
        ProgressUpdater progressUpdater = transport.getProgressUpdater();

        if (progressUpdater != null) {
            transport.cancel();
            progressUpdater.cancel(info); // cancel will call this method again
        } else {
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
