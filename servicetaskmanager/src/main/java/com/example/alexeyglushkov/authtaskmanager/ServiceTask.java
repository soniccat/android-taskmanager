package com.example.alexeyglushkov.authtaskmanager;

import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder;
import com.example.alexeyglushkov.taskmanager.task.SimpleTask;

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
}
