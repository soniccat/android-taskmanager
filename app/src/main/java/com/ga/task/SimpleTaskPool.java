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

    //TODO: think about weakref
    private List<TaskPoolListener> listeners;
    private Handler handler;

    //TODO: think about a map
    private List<Task> tasks;

    private Object userData;

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

        //TaskManager must set Starting status on the current thread
        task.getPrivate().setTaskStatus(Task.Status.Starting);

        Tools.runOnHandlerThread(handler, new Runnable() {
            @Override
            public void run() {
                addTaskOnThread(task);
            }
        });
    }

    private void addTaskOnThread(Task task) {
        checkHandlerThread();

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
            listener.onTaskAdded(this,task);
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
                        listener.onTaskRemoved(SimpleTaskPool.this, task);
                    }
                }
            }
        });
    }

    @Override
    public Task getTask(String taskId) {
        checkHandlerThread();

        for (Task task : tasks) {
            if(task.getTaskId() != null && task.getTaskId().equals(taskId)) {
                return task;
            }
        }

        return null;
    }

    @Override
    public int getTaskCount() {
        checkHandlerThread();

        return tasks.size();
    }

    @Override
    public List<Task> getTasks() {
        checkHandlerThread();

        return tasks;
    }

    @Override
    public void setUserData(Object data) {
        this.userData = data;
    }

    @Override
    public Object getUserData() {
        return userData;
    }

    @Override
    public void addListener(TaskPoolListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(TaskPoolListener listener) {
        listeners.remove(listener);
    }

    private void checkHandlerThread() {
        Assert.assertEquals(Looper.myLooper(),handler.getLooper());
    }
}
