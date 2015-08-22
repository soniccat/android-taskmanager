package com.ga.task;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;

import junit.framework.Assert;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by alexeyglushkov on 20.09.14.
 */
public class SimpleTaskManager implements TaskManager, TaskPool.TaskPoolListener {

    static final String TAG = "SimpleTaskManager";

    HandlerThread handlerThread;
    Handler handler;
    Handler callbackHandler;
    TaskExecutor taskExecutor;

    TaskPool loadingTasks;
    TaskProvider waitingTasks;
    List<TaskProvider> taskProviders; //always sorted by priority

    SparseArray<Float> limits;
    SparseArray<Integer> usedSpace; //type -> task count from loadingTasks

    public int maxLoadingTasks;

    boolean needUpdateSnapshot;
    SimpleTaskManagerSnapshot snapshot;
    List<WeakReference<OnSnapshotChangedListener>> snapshotChangedListeners;

    public SimpleTaskManager(int maxLoadingTasks) {
        this.taskExecutor = new SimpleTaskExecutor();
        this.maxLoadingTasks = maxLoadingTasks;

        handlerThread = new HandlerThread("SimpleLoader Thread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        callbackHandler = new Handler(Looper.myLooper());

        loadingTasks = new SimpleTaskPool(handler);
        loadingTasks.addListener(this);

        waitingTasks = new PriorityTaskProvider(handler, "TaskManagerProviderId");
        waitingTasks.addListener(this);

        taskProviders = new ArrayList<TaskProvider>();

        limits = new SparseArray<Float>();
        usedSpace = new SparseArray<Integer>();

        snapshotChangedListeners = new ArrayList<WeakReference<OnSnapshotChangedListener>>();
    }

    @Override
    public Handler getHandler() {
        return handler;
    }

    @Override
    public void put(final Task task) {
        Assert.assertEquals(task.getTaskStatus(), Task.Status.NotStarted);
        if (!Tasks.isTaskReadyToStart(task)) {
            Log.d(TAG, "Can't put task " + task.getClass().toString() + " because it has been added " + task.getTaskStatus().toString());
            return;
        }

        // TODO: actually I think that problem of adding the same task is not important
        // TaskManager must set Waiting status on the current thread
        task.getPrivate().setTaskStatus(Task.Status.Waiting);

        Tools.runOnHandlerThread(handler, new Runnable() {
            @Override
            public void run() {
                waitingTasks.addTask(task);
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
        Tools.runOnHandlerThread(handler, new Runnable() {
            @Override
            public void run() {
                cancelTaskOnThread(task, info);
            }
        });
    }

    @Override
    public void addTaskProvider(final TaskProvider provider) {
        Assert.assertEquals(provider.getHandler(), handler);
        Assert.assertNotNull(provider.getTaskProviderId());

        Tools.runOnHandlerThread(handler, new Runnable() {
            @Override
            public void run() {
                addTaskProviderOnThread(provider);
            }
        });
    }

    private void addTaskProviderOnThread(TaskProvider provider) {
        checkHandlerThread();

        TaskProvider oldTaskProvider = getTaskProvider(provider.getTaskProviderId());

        if (oldTaskProvider != null) {
            taskProviders.remove(oldTaskProvider);
        }

        provider.addListener(this); //for snapshots

        // add in sorted array
        int insertIndex = 0;
        for (int i=0; i<taskProviders.size(); ++i) {
            TaskProvider p = taskProviders.get(i);
            if (provider.getPriority() > p.getPriority()) {
                insertIndex = i;
                break;
            }
        }

        taskProviders.add(insertIndex, provider);
    }

    public void setTaskProviderPriority(final TaskProvider provider, final int priority) {
        Tools.runOnHandlerThread(handler, new Runnable() {
            @Override
            public void run() {
                setTaskProviderPriorityOnThread(provider, priority);
            }
        });
    }

    private void setTaskProviderPriorityOnThread(TaskProvider provider, int priority) {
        checkHandlerThread();

        provider.setPriority(priority);
        Collections.sort(taskProviders, new Comparator<TaskProvider>() {
            @Override
            public int compare(TaskProvider lhs, TaskProvider rhs) {
                return Tools.reverseIntCompare(lhs.getPriority(), rhs.getPriority());
            }
        });
    }

    public TaskProvider getTaskProvider(String id) {
        checkHandlerThread();

        for (TaskProvider taskProvider : taskProviders) {
            if (taskProvider.getTaskProviderId().equals(id)) {
                return taskProvider;
            }
        }

        return null;
    }

    @Override
    public void setTaskExecutor(TaskExecutor executor) {
        this.taskExecutor = executor;
    }

    // == TaskPool.TaskPoolListener

    /*
    @Override
    public boolean canAddTask(TaskPool pool, Task task) {
        final boolean isLoadingPool = pool == loadingTasks;

        boolean result = true;
        if (isLoadingPool)  {
            result = true;
        } else {
            // TODO: take from taskProvider
        }

        return result;
    }
    */

    @Override
    public void onTaskAdded(final TaskPool pool, final Task task) {
        checkHandlerThread();

        final boolean isLoadingPool = pool == loadingTasks;
        if (snapshot != null) {
            Tools.runOnHandlerThread(callbackHandler, new Runnable() {
                @Override
                public void run() {
                    if (isLoadingPool) {
                        snapshot.updateUsedLoadingSpace(task.getTaskType(), true);
                    } else {
                        snapshot.updateWaitingTaskInfo(task.getTaskType(), true);
                    }
                }
            });
        }

        //run on the next iteration to give ability to handle added event for other listeners before moving the task to the loading queue
        //otherwise removed event will be sent before added for TaskPool.TaskPoolListener (see functions below)
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (handleTaskLoadPolicy(task)) {
                    checkTasksToRunOnThread();

                } else if (!isLoadingPool) {
                    cancelTaskOnThread(task, null);
                }
            }
        });
    }

    @Override
    public void onTaskRemoved(TaskPool pool, final Task task) {
        checkHandlerThread();

        if (snapshot != null) {
            final boolean isLoadingPool = pool == loadingTasks;

            Tools.runOnHandlerThread(callbackHandler, new Runnable() {
                @Override
                public void run() {
                    if (isLoadingPool) {
                        snapshot.updateUsedLoadingSpace(task.getTaskType(), false);
                    } else {
                        snapshot.updateWaitingTaskInfo(task.getTaskType(), false);
                    }
                }
            });
        }

        /*if (task.getTaskStatus() == Task.Status.Cancelled) {
            cancelTaskOnThread(task, null);
        }*/
    }

    // ==

    @Override
    public void removeTaskProvider(final TaskProvider provider) {
        Tools.runOnHandlerThread(handler, new Runnable() {
            @Override
            public void run() {
                removeTaskProviderOnThread(provider);
            }
        });
    }

    private void removeTaskProviderOnThread(TaskProvider provider) {
        checkHandlerThread();

        taskProviders.remove(provider);
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

                if (snapshot != null) {
                    Tools.runOnHandlerThread(callbackHandler, new Runnable() {
                        @Override
                        public void run() {
                            snapshot.setLoadingLimit(taskType, availableQueuePart);
                        }
                    });
                }
            }
        });
    }

    public void setWaitingTaskProvider(TaskProvider provider) {
        Assert.assertEquals(provider.getHandler(), handler);

        this.waitingTasks = provider;
    }

    @Override
    public void startSnapshotRecording() {
        if (needUpdateSnapshot) {
            return;
        }

        needUpdateSnapshot = true;

        Tools.runOnHandlerThread(handler, new Runnable() {
            @Override
            public void run() {
                final SimpleTaskManagerSnapshot newSnapshot = new SimpleTaskManagerSnapshot();

                Tools.runOnHandlerThread(callbackHandler, new Runnable() {
                    @Override
                    public void run() {
                        snapshot = newSnapshot;
                        snapshot.setMaxQueueSize(maxLoadingTasks);
                    }
                });
            }
        });
    }

    @Override
    public void stopSnapshotRecording() {
        if (!needUpdateSnapshot) {
            return;
        }

        Tools.runOnHandlerThread(callbackHandler, new Runnable() {
            @Override
            public void run() {
                needUpdateSnapshot = false;
                snapshot = null;
            }
        });
    }

    @Override
    public TaskManagerSnapshot getSnapshot() {
        Assert.assertEquals(Thread.currentThread(), callbackHandler);

        return snapshot;
    }

    @Override
    public void addSnapshotListener(OnSnapshotChangedListener listener) {
        snapshotChangedListeners.add(new WeakReference<OnSnapshotChangedListener>(listener));
    }

    @Override
    public void removeSnapshotListener(OnSnapshotChangedListener listener) {
        int i = 0;
        for (WeakReference<OnSnapshotChangedListener> listenerRef : snapshotChangedListeners) {
            if (listenerRef.get() != null) {
                snapshotChangedListeners.remove(i);
                break;
            }

            ++i;
        }
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

    private void checkTasksToRunOnThread() {
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

    private Task takeTaskToRunOnThread() {
        checkHandlerThread();

        List<Integer> taskTypesToFilter = getTaskTypeFilter();
        Task topWaitingTask = this.waitingTasks.getTopTask(taskTypesToFilter);

        int topPriorityTaskIndex = -1;
        Task topPriorityTask = null;
        int topPriority = -1;

        if (topWaitingTask != null) {
            topPriorityTask = topWaitingTask;
            topPriority = topWaitingTask.getTaskPriority();
        }

        int i = 0;
        for (TaskProvider provider : taskProviders) {
            if (provider != null) {
                Task t = provider.getTopTask(taskTypesToFilter);
                if ( t != null && t.getTaskPriority() > topPriority) {
                    topPriorityTask = t;
                    topPriorityTaskIndex = i;
                }
            }

            ++i;
        }

        if (topPriorityTask != null && !reachedLimit(topPriorityTask.getTaskType())) {
            if (topPriorityTaskIndex == -1 && topPriorityTask != null) {
                topPriorityTask = waitingTasks.takeTopTask(taskTypesToFilter);

            } else if (topPriorityTaskIndex != -1) {
                topPriorityTask = taskProviders.get(topPriorityTaskIndex).takeTopTask(taskTypesToFilter);
            }
        } else {
            topPriorityTask = null;
        }

        return topPriorityTask;
    }

    private List<Integer> getTaskTypeFilter() {
        List<Integer> taskTypesToFilter = new ArrayList<Integer>();
        for (int i = 0; i < limits.size(); i++) {
            if (reachedLimit(limits.keyAt(i))) {
                taskTypesToFilter.add(limits.keyAt(i));
            }
        }
        return taskTypesToFilter;
    }

    private void startTaskOnThread(final Task task) {
        checkHandlerThread();
        logTask(task, "Task assert check");
        Assert.assertTrue(Tasks.isTaskReadyToStart(task));

        logTask(task, "Task started");
        task.getPrivate().setTaskStatus(Task.Status.Started);
        task.getPrivate().setTaskStartDate(new Date());

        final Task.Callback originalCallback = task.getTaskCallback();
        Task.Callback callback = new Task.Callback() {
            @Override
            public void finished(final boolean cancelled) {
                Tools.runOnHandlerThread(handler, new Runnable() {
                    @Override
                    public void run() {
                        logTask(task, "Task finished");
                        updateUsedSpace(task.getTaskType(), false);

                        Task.Status newStatus = cancelled ? Task.Status.Cancelled : Task.Status.Finished;
                        handleTaskCompletionOnThread(task, originalCallback, newStatus);
                    }
                });
            }
        };
        task.setTaskCallback(callback);
        taskExecutor.executeTask(task);
    }

    private void logTask(Task task, String prefix) {
        Log.d(TAG, prefix + " " + task.getClass().toString() + "(" + task.getTaskStatus() + ")" + " id= " + task.getTaskId() + " priority= " + task.getTaskPriority() + " time " + task.getTaskDuration());
    }

    private void checkHandlerThread() {
        Assert.assertEquals(Thread.currentThread(), handlerThread);
    }

    public void handleTaskCompletionOnThread(final Task task, final Task.Callback callback, final Task.Status status) {
        checkHandlerThread();
        task.getPrivate().setTaskStatus(status);
        task.getPrivate().clearAllListeners();

        Tools.runOnHandlerThread(callbackHandler, new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.finished(status == Task.Status.Cancelled);
                }
            }
        });

        //TODO: for the another status like cancelled new task won't be started
        //but we cant't just call checkTasksToRunOnThread due to Task.LoadPolicy.CancelAdded
        //because we want to have new task already in waiting queue but now it isn't
        if (status == Task.Status.Finished) {
            checkTasksToRunOnThread();
        }
    }

    void cancelTaskOnThread(Task task, final Object info) {
        checkHandlerThread();

        if (!task.getPrivate().getNeedCancelTask()) {
            Task.Status st = task.getTaskStatus();
            task.getPrivate().cancelTask(info);

            if (st == Task.Status.Waiting) {
                //waitingTasks.removeTask(task);
                handleTaskCompletionOnThread(task, task.getTaskCallback(), Task.Status.Cancelled);

                for (TaskProvider taskProvider : taskProviders) {
                    taskProvider.removeTask(task);
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
        Log.d(TAG, "loading task count " + loadingTasks.getTaskCount());
    }

    void triggerOnSnapshotListeners() {
        List<WeakReference<OnSnapshotChangedListener>> oldListeners = new ArrayList<WeakReference<OnSnapshotChangedListener>>();

        for (WeakReference<OnSnapshotChangedListener> listenerRef : snapshotChangedListeners) {
            if (listenerRef.get() != null) {
                listenerRef.get().onSnapshotChanged(this.snapshot);
            } else {
                oldListeners.add(listenerRef);
            }
        }
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
            for (TaskProvider taskProvider : taskProviders) {
                waitingTaskCount += taskProvider.getTaskCount();

                for (Task task : taskProvider.getTasks()) {
                    int count = waitingTaskInfo.get(task.getTaskType(), 0);
                    ++count;
                    waitingTaskInfo.put(task.getTaskType(), count);
                }
            }

            maxQueueSize = maxLoadingTasks;

            loadingLimits = limits.clone();
            usedLoadingSpace = usedSpace.clone();
        }

        public void setMaxQueueSize(int count) {
            maxQueueSize = count;
            triggerOnSnapshotListeners();
        }

        public void setLoadingLimit(int taskType, float availableQueuePart) {
            if (availableQueuePart == -1.0f) {
                loadingLimits.remove(taskType);
            } else {
                loadingLimits.put(taskType, availableQueuePart);
            }

            triggerOnSnapshotListeners();
        }

        private void updateUsedLoadingSpace(int taskType, boolean add) {
            Integer count = usedLoadingSpace.get(taskType, 0);
            if (add) {
                ++count;
                ++loadingTaskCount;
            } else {
                --count;
                --loadingTaskCount;

                if (count < 0) {
                    count = 0;
                }

                if (loadingTaskCount < 0) {
                    loadingTaskCount = 0;
                }
            }

            usedLoadingSpace.put(taskType, count);
            triggerOnSnapshotListeners();
        }

        private void updateWaitingTaskInfo(int taskType, boolean add) {
            int count = waitingTaskInfo.get(taskType, 0);
            if (add) {
                ++waitingTaskCount;
                ++count;
            } else {
                --waitingTaskCount;
                --count;

                if (count < 0) {
                    count = 0;
                }

                if (waitingTaskCount < 0) {
                    waitingTaskCount = 0;
                }
            }

            waitingTaskInfo.put(taskType, count);
            triggerOnSnapshotListeners();
        }


        //public

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
