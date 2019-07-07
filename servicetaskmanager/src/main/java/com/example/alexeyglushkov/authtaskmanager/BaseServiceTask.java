package com.example.alexeyglushkov.authtaskmanager;

import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder;
import com.example.alexeyglushkov.taskmanager.task.Task;
import com.example.alexeyglushkov.taskmanager.task.Tasks;

import androidx.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

import androidx.annotation.Nullable;

public class BaseServiceTask<T> implements ServiceTask<T> {
    protected Task task;

    //// Initialization

    public BaseServiceTask() {
    }

    public static <T> BaseServiceTask<T> fromSingle(Single<T> single) {
        return new BaseServiceTask<>(Tasks.INSTANCE.fromSingle(single));
    }

    public static <T> BaseServiceTask<T> fromMaybe(Maybe<T> maybe) {
        return new BaseServiceTask<>(Tasks.INSTANCE.fromMaybe(maybe));
    }

    public static BaseServiceTask<Object> fromCompletable(Completable completable) {
        return new BaseServiceTask<>(Tasks.INSTANCE.fromCompletable(completable));
    }

    protected BaseServiceTask(Task task) {
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
    public @NonNull Task getTask() {
        return task;
    }

    @Override
    public @Nullable T getResponse() {
        Object result = task.getTaskResult();
        return result != null ? (T)result : null;
    }

    @Override
    public @Nullable Error getCommandError() {
        return task.getTaskError();
    }

    @Override
    public boolean isCancelled() {
        return task.getTaskStatus() == Task.Status.Cancelled;
    }

    @Override
    public HttpUrlConnectionBuilder getConnectionBuilder() {
        return null;
    }

    @Override
    public int getResponseCode() {
        return 0;
    }
}
