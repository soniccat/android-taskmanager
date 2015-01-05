package com.ga.task;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
    Handler callbackHandler;

    TaskPool loadingTasks;
    TaskProvider waitingTasks;
    List<WeakReference<TaskProvider>> taskProviders;

    public int maxLoadingTasks;

    public SimpleTaskManager(int maxLoadingTasks) {
        this.maxLoadingTasks = maxLoadingTasks;

        handlerThread = new HandlerThread("SimpleLoader Thread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        callbackHandler = new Handler(Looper.myLooper());

        loadingTasks = new SimpleTaskPool(handler);
        waitingTasks = new PriorityTaskProvider(handler, new SimpleTaskPool(handler));
        taskProviders = new ArrayList<WeakReference<TaskProvider>>();
    }

    @Override
    public Handler getHandler() {
        return handler;
    }

    @Override
    public void put(final Task task) {
        assert (task.getTaskStatus() == Task.Status.NotStarted);

        boolean canStartTask = task.getTaskStatus() == Task.Status.NotStarted;
        if (!canStartTask) {
            Log.d(TAG, "Can't put task " + task.getClass().toString() + " because it's already started " + task.getTaskStatus().toString());
        }

        //Task Manager must set Waiting status on the current thread
        task.setTaskStatus(Task.Status.Waiting);

        Tools.runOnHandlerThread(handler, new Runnable() {
            @Override
            public void run() {
                putOnThread(task);
            }
        });
    }

    @Override
    public void cancel(final Task task, final Object info) {
        Tools.runOnHandlerThread(handler,new Runnable() {
            @Override
            public void run() {
                cancelTaskOnThread(task, info);
            }
        });
    }

    @Override
    public void addTaskProvider(TaskProvider provider) {
        assert provider.getHandler() == handler;

        provider.addListener(new TaskProvider.TaskProviderListener() {
            @Override
            public void onTaskAdded(Task task) {
                checkTasksToRunOnThread();
            }

            @Override
            public void onTaskRemoved(Task task) {

            }
        });

        taskProviders.add(new WeakReference<TaskProvider>(provider));
    }

    public void setWaitingTaskProvider(TaskProvider provider) {
        this.waitingTasks = provider;
    }

    // Private

    // functions called on a handler's thread
    // actual work

    private void putOnThread(Task task) {
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

        Task topWaitingTask = this.waitingTasks.getTopTask();
        ArrayList<WeakReference<TaskProvider>> emptyReferences = new ArrayList<WeakReference<TaskProvider>>();

        int topPriorityTaskIndex = -1;
        Task topPriorityTask = null;
        int topPriority = -1;

        if (topWaitingTask != null) {
            topPriorityTask = topWaitingTask;
            topPriority = topWaitingTask.getTaskPriority();
        }

        int i = 0;
        for (WeakReference<TaskProvider> provider : taskProviders) {
            if (provider.get() != null) {
                Task t = provider.get().getTopTask();
                if ( t != null && t.getTaskPriority() > topPriority) {
                    topPriorityTask = t;
                    topPriorityTaskIndex = i;
                }
            } else {
                emptyReferences.add(provider);
            }

            ++i;
        }

        if (topPriorityTaskIndex == -1 && topPriorityTask != null) {
            topPriorityTask = waitingTasks.takeTopTask();

        } else if (topPriorityTaskIndex != -1) {
            topPriorityTask = taskProviders.get(topPriorityTaskIndex).get().takeTopTask();
        }

        taskProviders.removeAll(emptyReferences);
        return topPriorityTask;
    }

    private void startTaskOnThread(final Task task) {
        checkHandlerThread();
        checkTaskIsWaiting(task);

        logTask(task, "Task started");
        task.setTaskStatus(Task.Status.Started);
        task.setTaskStartDate(new Date());

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
        Log.d(TAG, prefix + " " + task.getClass().toString() + " id= " + task.getTaskId() + " priority= " + task.getTaskPriority() + " time " + task.getTaskDuration());
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

        Tools.runOnHandlerThread(callbackHandler, new Runnable() {
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
        Log.d(TAG, "waiting tasks " + this.waitingTasks.getTaskPool().getTaskCount());
    }

    void addLoadingTaskOnThread(Task task) {
        checkHandlerThread();

        this.loadingTasks.addTask(task);
    }
}
