package com.ga.task;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alexeyglushkov on 20.09.14.
 */
public class SimpleTaskManager implements TaskManager {

    static final String TAG = "SimpleTaskManager";

    HandlerThread handlerThread;
    Handler handler;

    TaskPool loadingTasks;
    TaskProvider waitingTasks;

    public int maxLoadingTasks;

    public SimpleTaskManager(int maxLoadingTasks) {
        this.maxLoadingTasks = maxLoadingTasks;

        handlerThread = new HandlerThread("SimpleLoader Thread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());

        loadingTasks = new SimpleTaskPool(handler);
        waitingTasks = new PriorityTaskProvider(handler, new SimpleTaskPool(handler));
    }

    @Override
    public Handler getHandler() {
        return handler;
    }

    //TODO: add other thread support
    @Override
    public void put(final Task task, final Task.Callback callback) {
        assert (Looper.myLooper() == Looper.getMainLooper());
        assert (task.getTaskStatus() == Task.Status.NotStarted);

        boolean canStartTask = task.getTaskStatus() == Task.Status.NotStarted;
        if (!canStartTask) {
            Log.d(TAG, "Can't put task " + task.getClass().toString() + " because it's already started " + task.getTaskStatus().toString());
        }

        //Task Manager must set Waiting status on the main thread
        task.setTaskStatus(Task.Status.Waiting);

        Tools.runOnHandlerThread(handler, new Runnable() {
            @Override
            public void run() {
                putOnThread(task, callback);
            }
        });
    }

    @Override
    public void cancel(final Task task, final Object info) {
        assert (Looper.myLooper() == Looper.getMainLooper());

        Tools.runOnHandlerThread(handler,new Runnable() {
            @Override
            public void run() {
                cancelTaskOnThread(task, info);
            }
        });
    }

    // Private

    // functions called on a handler's thread
    // actual work

    private void putOnThread(Task task, Task.Callback callback) {
        checkHandlerThread();

        //search for the task with the same id
        if (task.getTaskId() != null) {

            Task addedTask = getWaitingTaskByIdOnThread(task.getTaskId());
            if (addedTask == null) {
                addedTask = getLoadingTaskByIdOnThread(task.getTaskId());
            }

            if (addedTask != null) {
                if (task.getLoadPolicy() == Task.LoadPolicy.CancelAdded) {
                    cancelTaskOnThread(addedTask, null);
                } else {
                    Log.d(TAG, "The task was skipped due to the Load Policy " + task.getLoadPolicy().toString() + task.getClass().toString() + " " + task.getTaskId() + " " + task.getTaskStatus().toString());
                    return;
                }
            }
        }

        task.setTaskCallback(callback);
        addWaitingTaskOnThread(task);
        checkTasksToRunOnThread();
    }

    private Task getWaitingTaskByIdOnThread(String taskId) {
        checkHandlerThread();
        return waitingTasks.getTaskPool().getTask(taskId);
    }

    private Task getLoadingTaskByIdOnThread(String taskId) {
        checkHandlerThread();
        return loadingTasks.getTask(taskId);
    }

    void checkTasksToRunOnThread() {
        checkHandlerThread();

        if (this.loadingTasks.getTaskCount() < this.maxLoadingTasks) {
            final Task task = takeTaskToRunOnThread();

            if (task != null) {
                addLoadingTaskOnThread(task);
                startTaskOnThread(task);
            }
        }
    }

    Task takeTaskToRunOnThread() {
        checkHandlerThread();

        return this.waitingTasks.takeTopTask();
    }

    private void startTaskOnThread(final Task task) {
        checkHandlerThread();
        checkTaskIsWaiting(task);

        logTask(task, "Task started");
        task.setTaskStatus(Task.Status.Started);

        final Task.Callback originalCallback = task.getTaskCallback();
        Task.Callback callback = new Task.Callback() {
            @Override
            public void finished() {
                Tools.runOnHandlerThread(handler, new Runnable() {
                    @Override
                    public void run() {
                        logTask(task, "Task finished");
                        handleTaskCompletionOnThread(task, originalCallback, Task.Status.Finished);
                    }
                });
            }
        };
        task.setTaskCallback(callback);
        task.startTask();
    }

    private void logTask(Task task, String prefix) {
        Log.d(TAG, prefix + " " + task.getClass().toString() + " " + task.getTaskId());
    }

    private void checkHandlerThread() {
        assert Thread.currentThread() == handlerThread;
    }

    private void checkTaskIsWaiting(Task task) {
        assert Tasks.isTaskReadyToStart(task);
    }

    public void handleTaskCompletionOnThread(final Task task, final Task.Callback callback, Task.Status status) {
        checkHandlerThread();
        task.setTaskStatus(status);

        Tools.postOnMainLoop(new Runnable() {
            @Override
            public void run() {
                callback.finished();
            }
        });

        if (status == Task.Status.Finished) {
            checkTasksToRunOnThread();
        }
    }

    void cancelTaskOnThread(Task task, final Object info) {
        checkHandlerThread();
        task.cancelTask(info);

        if (task.getTaskStatus() == Task.Status.Waiting) {
            handleTaskCompletionOnThread(task, task.getTaskCallback(), Task.Status.Cancelled);

            logTask(task, "Cancelled");
        }
    }

    // helpers

    void addWaitingTaskOnThread(Task task) {
        checkHandlerThread();

        this.waitingTasks.getTaskPool().addTask(task);
        Log.d(TAG,"waiting tasks " + this.waitingTasks.getTaskPool().getTaskCount());
    }

    void addLoadingTaskOnThread(Task task) {
        checkHandlerThread();

        this.loadingTasks.addTask(task);
    }
}
