package com.ga.task;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;

import junit.framework.Assert;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    SparseArray<Float> limits;
    SparseArray<Integer> usedSpace; //type -> task count from loadingTasks

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

        limits = new SparseArray<Float>();
        usedSpace = new SparseArray<Integer>();
    }

    @Override
    public Handler getHandler() {
        return handler;
    }

    @Override
    public void put(final Task task) {
        Assert.assertEquals(task.getTaskStatus(), Task.Status.NotStarted);

        if (!Tasks.isTaskReadyToStart(task)) {
            Log.d(TAG, "Can't put task " + task.getClass().toString() + " because it's already started " + task.getTaskStatus().toString());
            return;
        }

        task.setTaskStatus(Task.Status.Waiting);

        Tools.runOnHandlerThread(handler, new Runnable() {
            @Override
            public void run() {
                waitingTasks.getTaskPool().addTask(task);
                putOnThread(task);
            }
        });
    }

    @Override
    public void startImmediately(final Task task) {
        Tools.runOnHandlerThread(handler, new Runnable() {
            @Override
            public void run() {
                if (handleTaskLoadPolicy(task)) {
                    startTaskOnThread(task);
                }
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
        Assert.assertEquals(provider.getHandler(),handler);

        provider.addListener(new TaskProvider.TaskProviderListener() {
            @Override
            public void onTaskAdded(Task task) {
                checkTasksToRunOnThread();
            }

            @Override
            public void onTaskRemoved(Task task) {
                cancelTaskOnThread(task, null);
            }
        });

        taskProviders.add(new WeakReference<TaskProvider>(provider));
    }

    @Override
    public void setLimit(final int taskType, final float availableQueuePart) {
        Tools.runOnHandlerThread(handler, new Runnable() {
            @Override
            public void run() {
                if (availableQueuePart == -1.0f) {
                    limits.remove(taskType);
                } else {
                    limits.put(taskType, availableQueuePart);
                }
            }
        });
    }

    public void setWaitingTaskProvider(TaskProvider provider) {
        Assert.assertEquals(provider.getTaskPool().getHandler(), handler);
        Assert.assertEquals(provider.getHandler(), handler);

        this.waitingTasks = provider;
    }

    @Override
    public TaskManagerSnapshot createSnapshot() {
        checkHandlerThread();

        return new SimpleTaskManagerSnapshot();
    }

    // Private

    // functions called on a handler's thread

    private void putOnThread(Task task) {
        checkHandlerThread();

        if (handleTaskLoadPolicy(task)) {
            checkTasksToRunOnThread();
        }
    }

    private boolean handleTaskLoadPolicy(Task task) {
        checkHandlerThread();

        //search for the task with the same id
        if (task.getTaskId() != null) {
            Task addedTask = loadingTasks.getTask(task.getTaskId());
            if (addedTask != null) {
                if (task.getLoadPolicy() == Task.LoadPolicy.CancelAdded) {
                    cancelTaskOnThread(addedTask, null);
                } else {
                    Log.d(TAG, "The task was skipped due to the Load Policy " + task.getLoadPolicy().toString() + task.getClass().toString() + " " + task.getTaskId() + " " + task.getTaskStatus().toString());
                    return false;
                }
            }
        }
        return true;
    }

    void checkTasksToRunOnThread() {
        checkHandlerThread();

        if (this.loadingTasks.getTaskCount() < this.maxLoadingTasks) {
            final Task task = takeTaskToRunOnThread();

            if (task != null) {
                addLoadingTaskOnThread(task);
                updateUsedSpace(task.getTaskType(), true);
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

        if (topPriorityTask != null && !reachedLimit(topPriorityTask.getTaskType())) {
            if (topPriorityTaskIndex == -1 && topPriorityTask != null) {
                topPriorityTask = waitingTasks.takeTopTask();

            } else if (topPriorityTaskIndex != -1) {
                topPriorityTask = taskProviders.get(topPriorityTaskIndex).get().takeTopTask();
            }
        } else {
            topPriorityTask = null;
        }

        taskProviders.removeAll(emptyReferences);
        return topPriorityTask;
    }

    private void startTaskOnThread(final Task task) {
        checkHandlerThread();
        Assert.assertTrue(Tasks.isTaskReadyToStart(task));

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
                        updateUsedSpace(task.getTaskType(), false);
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
        Assert.assertEquals(Thread.currentThread(), handlerThread);
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

        if (!task.getNeedCancelTask()) {
            Task.Status st = task.getTaskStatus();
            task.cancelTask(info);

            if (st == Task.Status.Waiting) {
                waitingTasks.getTaskPool().removeTask(task);

                for (WeakReference<TaskProvider> taskProvider : taskProviders) {
                    if (taskProvider.get() != null) {
                        taskProvider.get().getTaskPool().removeTask(task);
                    }
                }

                logTask(task, "Cancelled");
            }
        }
    }

    private boolean reachedLimit(int taskType) {
        if (limits.get(taskType,-1.0f) == -1.0f) {
            return false;
        }

        return (float)usedSpace.get(taskType,0) / (float)maxLoadingTasks >= limits.get(taskType, 0.0f);
    }

    private void updateUsedSpace(int taskType, boolean add) {
        Integer count = usedSpace.get(taskType, 0);
        if (add) {
            ++count;
        } else {
            Assert.assertTrue(count > 0);
            --count;
        }

        usedSpace.put(taskType, count);
    }

    // helpers

    void addLoadingTaskOnThread(Task task) {
        checkHandlerThread();

        this.loadingTasks.addTask(task);
    }

    private class SimpleTaskManagerSnapshot implements TaskManagerSnapshot {
        int loadingTaskCount;
        int waitingTaskCount;
        int maxQueueSize;
        SparseArray<Float> loadingLimits;
        SparseArray<Integer> usedLoadingSpace;
        SparseArray<Integer> waitingTaskInfo;

        public SimpleTaskManagerSnapshot() {
            loadingTaskCount = loadingTasks.getTaskCount();

            waitingTaskInfo = new SparseArray<Integer>();
            for (WeakReference<TaskProvider> taskProvider : taskProviders) {
                if (taskProvider.get() != null) {
                    waitingTaskCount += taskProvider.get().getTaskPool().getTaskCount();

                    for (Task task : taskProvider.get().getTaskPool().getTasks()) {
                        int count = waitingTaskInfo.get(task.getTaskType(), 0);
                        ++count;
                        waitingTaskInfo.put(task.getTaskType(), count);
                    }
                }
            }

            maxQueueSize = maxLoadingTasks;

            loadingLimits = limits.clone();
            usedLoadingSpace = usedSpace.clone();
        }

        @Override
        public int getLoadingTasksCount() {
            return loadingTaskCount;
        }

        @Override
        public int getWaitingTasksCount() {
            return waitingTaskCount;
        }

        @Override
        public int getMaxQueueSize() {
            return maxQueueSize;
        }

        @Override
        public SparseArray<Float> getLoadingLimits() {
            return loadingLimits;
        }

        @Override
        public SparseArray<Integer> getUsedLoadingSpace() {
            return usedLoadingSpace;
        }

        @Override
        public SparseArray<Integer> getWaitingTaskInfo() {
            return waitingTaskInfo;
        }
    }
}
