package com.ga.task;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexeyglushkov on 30.12.14.
 */
public class SimpleTaskPool implements TaskPool, Task.StatusListener {
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
        Tools.runOnHandlerThread(handler, new Runnable() {
            @Override
            public void run() {
                task.addTaskStatusListener(SimpleTaskPool.this);
                tasks.add(task);

                for (TaskPoolListener listener : listeners) {
                    listener.onTaskAdded(task);
                }
            }
        });
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
                tasks.remove(task);

                for (TaskPoolListener listener : listeners) {
                    listener.onTaskRemoved(task);
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
        assert Looper.getMainLooper() == handler.getLooper();
    }
}
