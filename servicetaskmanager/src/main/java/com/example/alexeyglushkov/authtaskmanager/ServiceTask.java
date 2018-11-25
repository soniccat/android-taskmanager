package com.example.alexeyglushkov.authtaskmanager;

import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder;
import com.example.alexeyglushkov.taskmanager.task.SimpleTask;
import com.example.alexeyglushkov.taskmanager.task.Task;
import com.example.alexeyglushkov.taskmanager.task.TaskImpl;

/**
 * Created by alexeyglushkov on 17.07.16.
 */
public abstract class ServiceTask<T> extends BaseServiceTask<T> {
    private Task task;

    public ServiceTask() {
        setTask(new TaskImpl() {
            @Override
            public void startTask(Callback callback) {
                super.startTask(callback);

                onStart();

                getPrivate().handleTaskCompletion(callback);
            }
        });
    }

    public abstract void onStart();

    protected void setResult(T result) {
        task.getPrivate().setTaskResult(result);
    }

    protected void setError(Error err) {
        task.getPrivate().setTaskError(err);
    }
}
