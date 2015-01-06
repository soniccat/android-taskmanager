package com.ga.task;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexeyglushkov on 30.12.14.
 */
public class SimpleTaskPool implements TaskPool, Task.StatusListener {
    static final String TAG = "SimpleTaskPool";

    List<TaskPoolListener> listeners;
    Handler handler;

    //TODO: think about a map
    List<Task> tasks;

    public SimpleTaskPool(Handler handler) {
        tasks = new ArrayList<Task>();
        listeners = new ArrayList<TaskPoolListener>();
        setHandler(handler);
    }

    @Override
    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public Handler getHandler() {
        return handler;
    }

    @Override
    public void addTask(final Task task) {
        if (!Tasks.isTaskReadyToStart(task)) {
            Log.d(TAG, "Can't put task " + task.getClass().toString() + " because it's already started " + task.getTaskStatus().toString());
            return;
        }

        //Task Manager must set Waiting status on the current thread
        task.setTaskStatus(Task.Status.Waiting);

        Tools.runOnHandlerThread(handler, new Runnable() {
            @Override
            public void run() {
                addTaskOnThread(task);
            }
        });
    }

    private void addTaskOnThread(Task task) {
        handlerThreadCheck();

        task.addTaskStatusListener(this);

        //search for the task with the same id
        if (task.getTaskId() != null) {
            Task addedTask = getTask(task.getTaskId());
            if (addedTask != null) {
                if (task.getLoadPolicy() == Task.LoadPolicy.CancelAdded) {
                    removeTask(addedTask);
                } else {
                    Log.d(TAG, "The task was skipped due to the Load Policy " + task.getLoadPolicy().toString() + task.getClass().toString() + " " + task.getTaskId() + " " + task.getTaskStatus().toString());
                    return;
                }
            }
        }

        tasks.add(task);

        for (TaskPoolListener listener : listeners) {
            listener.onTaskAdded(task);
        }
    }

    public void onTaskStatusChanged(Task task, Task.Status oldStatus, Task.Status newStatus) {
        if (Tasks.isTaskCompleted(task)) {
            removeTask(task);
        }
    }

    @Override
    public void removeTask(final Task task) {
        Tools.runOnHandlerThread(handler, new Runnable() {
            @Override
            public void run() {
                if (tasks.remove(task)) {
                    for (TaskPoolListener listener : listeners) {
                        listener.onTaskRemoved(task);
                    }
                }
            }
        });
    }

    @Override
    public Task getTask(String taskId) {
        handlerThreadCheck();

        for (Task task : tasks) {
            if(task.getTaskId().equals(taskId)) {
                return task;
            }
        }

        return null;
    }

    @Override
    public int getTaskCount() {
        handlerThreadCheck();

        return tasks.size();
    }

    @Override
    public List<Task> getTasks() {
        handlerThreadCheck();

        return tasks;
    }

    @Override
    public void addListener(TaskPoolListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(TaskPoolListener listener) {
        listeners.remove(listener);
    }

    private void handlerThreadCheck() {
        Assert.assertEquals(Looper.myLooper(),handler.getLooper());
    }
}
