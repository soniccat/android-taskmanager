package com.example.alexeyglushkov.authtaskmanager;

import com.example.alexeyglushkov.taskmanager.loader.http.TransportTask;
import com.example.alexeyglushkov.taskmanager.task.Task;

public abstract class BaseServiceTask<T> implements IServiceTask<T> {
    protected Task task;

    //// Initialization

    public BaseServiceTask() {
    }

    public BaseServiceTask(Task task) {
        this.task = task;
    }

    //// Interface methods

    @Override
    public void clear() {
        task.getPrivate().clear();
    }

    //// Setters / Getters

    // Setters

    public void setTask(Task task) {
        this.task = task;
    }

    // Getters

    @Override
    public Task getTask() {
        return task;
    }

    @Override
    public T getResponse() {
        Object result = task.getTaskResult();
        return result != null ? (T)result : null;
    }

    @Override
    public Error getCommandError() {
        return task.getTaskError();
    }

    @Override
    public boolean isCancelled() {
        return task.getTaskStatus() == Task.Status.Cancelled;
    }
}
