package com.example.alexeyglushkov.authtaskmanager;

import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder;
import com.example.alexeyglushkov.taskmanager.task.SimpleTask;
import com.example.alexeyglushkov.taskmanager.task.Task;

/**
 * Created by alexeyglushkov on 17.07.16.
 */
public abstract class ServiceTask extends SimpleTask implements IServiceTask {

    @Override
    public HttpUrlConnectionBuilder getConnectionBulder() {
        return null;
    }

    @Override
    public String getResponse() {
        return null;
    }

    @Override
    public int getResponseCode() {
        return 0;
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
    public void setServiceCommandCallback(final CommandCallback callback) {
        setTaskCallback(new Task.Callback() {
            @Override
            public void onCompleted(boolean cancelled) {
                callback.onCompleted(ServiceTask.this, getCommandError());
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
}
