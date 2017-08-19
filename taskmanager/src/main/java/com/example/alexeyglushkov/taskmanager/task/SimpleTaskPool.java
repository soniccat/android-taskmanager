package com.example.alexeyglushkov.taskmanager.task;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.alexeyglushkov.tools.HandlerTools;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexeyglushkov on 30.12.14.
 */
public class SimpleTaskPool implements TaskPool {
    static final String TAG = "SimpleTaskPool";

    //TODO: think about weakref
    protected List<TaskPoolListener> listeners;
    private Handler handler;

    //TODO: think about a map
    private List<Task> tasks;

    private Object userData;

    public SimpleTaskPool(Handler handler) {
        tasks = new ArrayList<>();
        listeners = new ArrayList<>();
        setHandler(handler);
    }

    @Override
    public void setHandler(Handler handler) {
        if (this.handler != null) {
            checkHandlerThread();
        }

        this.handler = handler;
    }

    @Override
    public Handler getHandler() {
        return handler;
    }

    @Override
    public void addTask(final Task task) {
        if (!Tasks.isTaskReadyToStart(task)) {
            Log.d(TAG, "Can't put task " + task.getClass().toString() + " because it has been added " + task.getTaskStatus().toString());
            return;
        }

        // TaskPool must set Waiting status on the current thread
        task.getPrivate().setTaskStatus(Task.Status.Waiting);

        HandlerTools.runOnHandlerThread(handler, new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "addTaskOnThread");
                addTaskOnThread(task);
            }
        });
    }

    private void addTaskOnThread(Task task) {
        checkHandlerThread();

        task.addTaskStatusListener(this);
        tasks.add(task);

        triggerOnTaskAdded(task);
    }

    protected void triggerOnTaskAdded(Task task) {
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
        HandlerTools.runOnHandlerThread(handler, new Runnable() {
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

    protected void checkHandlerThread() {
        Assert.assertEquals(Looper.myLooper(), handler.getLooper());
    }
}
