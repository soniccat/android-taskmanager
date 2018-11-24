package com.example.alexeyglushkov.authtaskmanager;

import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder;
import com.example.alexeyglushkov.taskmanager.task.SimpleTask;
import com.example.alexeyglushkov.taskmanager.task.Task;
import com.example.alexeyglushkov.taskmanager.task.TaskImpl;

/**
 * Created by alexeyglushkov on 17.07.16.
 */
public abstract class ServiceTask<T> implements IServiceTask<T> {
    private Task task;

    public ServiceTask() {
        this.task = new TaskImpl() {
            @Override
            public void startTask(Callback callback) {
                super.startTask(callback);

                onStart();

                getPrivate().handleTaskCompletion(callback);
            }
        };
    }

    public abstract void onStart();

    protected void setResult(T result) {
        task.getPrivate().setTaskResult(result);
    }

    protected void setError(Error err) {
        task.getPrivate().setTaskError(err);
    }

    @Override
    public HttpUrlConnectionBuilder getConnectionBuilder() {
        return null;
    }

    @Override
    public T getResponse() {
        return (T)task.getTaskResult();
    }

    @Override
    public int getResponseCode() {
        return 0;
    }

    @Override
    public Error getCommandError() {
        return task.getTaskError();
    }

    @Override
    public boolean isCancelled() {
        return task.getTaskStatus() == Task.Status.Cancelled;
    }

    @Override
    public Task getTask() {
        return task;
    }

    @Override
    public void clear() {
        task.getPrivate().clear();
    }
}
