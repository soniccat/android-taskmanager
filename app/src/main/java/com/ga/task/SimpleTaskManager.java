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

    private HandlerThread handlerThread;
    private Handler handler;
    private Handler callbackHandler;
    private TaskExecutor taskExecutor;
    private Object userData;
    private WeakRefList<TaskManagerListener> listeners;

    private TaskPool loadingTasks;
    private TaskProvider waitingTasks;
    private List<TaskProvider> taskProviders; //always sorted by priority

    private SparseArray<Float> limits;
    private SparseArray<Integer> usedSpace; //type -> task count from loadingTasks

    private int maxLoadingTasks;

    public SimpleTaskManager(int maxLoadingTasks) {
        this.taskExecutor = new SimpleTaskExecutor();
        this.maxLoadingTasks = maxLoadingTasks;

        handlerThread = new HandlerThread("SimpleTaskManager Thread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        callbackHandler = new Handler(Looper.myLooper());

        loadingTasks = new SimpleTaskPool(handler);
        loadingTasks.addListener(this);

        // TODO: consider putting it to taskProiders list
        waitingTasks = new PriorityTaskProvider(handler, "TaskManagerProviderId");
        waitingTasks.addListener(this);

        listeners = new WeakRefList<TaskManagerListener>();
        taskProviders = new ArrayList<TaskProvider>();

        limits = new SparseArray<Float>();
        usedSpace = new SparseArray<Integer>();
    }

    @Override
    public void setMaxLoadingTasks(int maxLoadingTasks) {
        this.maxLoadingTasks = maxLoadingTasks;
    }

    @Override
    public int getMaxLoadingTasks() {
        return maxLoadingTasks;
    }

    @Override
    public void addTask(final Task task) {
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
                addTaskOnThread(task);
            }
        });
    }

    @Override
    public int getLoadingTaskCount() {
        return loadingTasks.getTaskCount();
    }

    @Override
    public void startImmediately(final Task task) {
        Tools.runOnHandlerThread(handler, new Runnable() {
            @Override
            public void run() {
                if (handleTaskLoadPolicy(task)) {

                    // TODO: write tests for that logic
                    final Task.Callback oldCallBack = task.getTaskCallback();
                    task.setTaskCallback(new Task.Callback() {
                        @Override
                        public void finished(boolean cancelled) {
                            triggerOnTaskRemoved(task, true);

                            if (oldCallBack != null) {
                                oldCallBack.finished(cancelled);
                            }
                        }
                    });

                    triggerOnTaskAdded(task, true);
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

    public List<TaskProvider> getTaskProviders() {
        checkHandlerThread();

        return taskProviders;
    }

    private void addTaskProviderOnThread(TaskProvider provider) {
        checkHandlerThread();

        TaskProvider oldTaskProvider = getTaskProvider(provider.getTaskProviderId());
        if (oldTaskProvider != null) {
            taskProviders.remove(oldTaskProvider);
        }

        // add in sorted array
        int insertIndex = 0;
        for (int i=0; i<taskProviders.size(); ++i) {
            TaskProvider p = taskProviders.get(i);
            if (provider.getPriority() > p.getPriority()) {
                insertIndex = i;
                break;
            }
        }

        provider.addListener(this); //for snapshots
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

    @Override
    public void onTaskAdded(final TaskPool pool, final Task task) {
        checkHandlerThread();

        final boolean isLoadingPool = pool == loadingTasks;
        triggerOnTaskAdded(task, isLoadingPool);

        // run on the next cycle to give a chance to handle added event for other listeners
        // before moving the task to the loading queue
        // otherwise removed event will be sent before added for TaskPool.TaskPoolListener (see functions below)
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

        triggerOnTaskRemoved(task, pool == loadingTasks);
    }

    // == TaskPool interface

    @Override
    public Handler getHandler() {
        return handler;
    }

    @Override
    public void setHandler(Handler handler) {
        /*if (this.handler != null) {
            checkHandlerThread();
        }*/

        this.handler = handler;

        loadingTasks.setHandler(handler);
        waitingTasks.setHandler(handler);
        for (TaskProvider provider : taskProviders) {
            provider.setHandler(handler);
        }
    }

    @Override
    public void removeTask(Task task) {
        cancel(task, null);
    }

    @Override
    public Task getTask(String taskId) {
        checkHandlerThread();

        Task task = loadingTasks.getTask(taskId);

        if (task == null) {
            task = waitingTasks.getTask(taskId);
        }

        if (task == null) {
            for (TaskProvider provider : taskProviders) {
                task = provider.getTask(taskId);
                if (task != null) {
                    break;
                }
            }
        }

        return task;
    }

    @Override
    public int getTaskCount() {
        checkHandlerThread();

        int taskCount = loadingTasks.getTaskCount() + waitingTasks.getTaskCount();
        for (TaskProvider provider : taskProviders) {
            taskCount += provider.getTaskCount();
        }

        return taskCount;
    }

    @Override
    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<Task>();

        tasks.addAll(loadingTasks.getTasks());
        tasks.addAll(waitingTasks.getTasks());
        for (TaskProvider provider : taskProviders) {
            tasks.addAll(provider.getTasks());
        }

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
    public void removeListener(TaskManagerListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void addListener(TaskManagerListener listener) {
        listeners.add(new WeakReference<TaskManagerListener>(listener));
    }

    @Override
    public void addListener(TaskPoolListener listener) {
        addListener((TaskManagerListener) listener);
    }

    @Override
    public void removeListener(TaskPoolListener listener) {
        removeListener((TaskManagerListener) listener);
    }

    @Override
    public void onTaskStatusChanged(Task task, Task.Status oldStatus, Task.Status newStatus) {
        //unnecessary
    }

    private void triggerOnTaskAdded(Task task, boolean isLoadingQueue) {
        checkHandlerThread();

        for (WeakReference<TaskManagerListener> listener : listeners) {
            listener.get().onTaskAdded(this, task, isLoadingQueue);
        }
    }

    private void triggerOnTaskRemoved(Task task, boolean isLoadingQueue) {
        checkHandlerThread();

        for (WeakReference<TaskManagerListener> listener : listeners) {
            listener.get().onTaskRemoved(this, task, isLoadingQueue);
        }
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

                for (WeakReference<TaskManagerListener> listener : listeners) {
                    listener.get().onLimitsChanged(SimpleTaskManager.this, taskType, availableQueuePart);
                }

                /*
                if (snapshot != null) {
                    Tools.runOnHandlerThread(callbackHandler, new Runnable() {
                        @Override
                        public void run() {
                            snapshot.setLoadingLimit(taskType, availableQueuePart);
                        }
                    });
                }*/
            }
        });
    }

    public SparseArray<Float> getLimits() {
        checkHandlerThread();

        return limits.clone();
    }

    @Override
    public SparseArray<Integer> getUsedSpace() {
        return usedSpace.clone();
    }

    public void setWaitingTaskProvider(TaskProvider provider) {
        Assert.assertEquals(provider.getHandler(), handler);

        this.waitingTasks = provider;
    }

    // Private

    private void addTaskOnThread(Task task) {
        checkHandlerThread();

        if (handleTaskLoadPolicy(task)) {
            waitingTasks.addTask(task);
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

        int taskProviderIndex = -1;
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
                    taskProviderIndex = i;
                }
            }

            ++i;
        }

        if (topPriorityTask != null && !reachedLimit(topPriorityTask.getTaskType())) {
            if (taskProviderIndex == -1 && topPriorityTask != null) {
                topPriorityTask = waitingTasks.takeTopTask(taskTypesToFilter);

            } else if (taskProviderIndex != -1) {
                topPriorityTask = taskProviders.get(taskProviderIndex).takeTopTask(taskTypesToFilter);
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
        Assert.assertEquals(Looper.myLooper(), handler.getLooper());
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
                // the task will be removed from providers automatically
                handleTaskCompletionOnThread(task, task.getTaskCallback(), Task.Status.Cancelled);

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
}
