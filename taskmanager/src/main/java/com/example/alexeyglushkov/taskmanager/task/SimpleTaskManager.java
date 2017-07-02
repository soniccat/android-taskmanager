package com.example.alexeyglushkov.taskmanager.task;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;

import com.example.alexeyglushkov.streamlib.CancelError;
import com.example.alexeyglushkov.tools.HandlerTools;

import junit.framework.Assert;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

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
    private SafeList<TaskProvider> taskProviders;

    private SparseArray<Float> limits;
    private SparseArray<Integer> usedSpace; //type -> task count from loadingTasks

    private int maxLoadingTasks;

    public SimpleTaskManager(int maxLoadingTasks) {
        init(maxLoadingTasks, null);
    }

    public SimpleTaskManager(int maxLoadingTasks, Handler inHandler) {
        init(maxLoadingTasks, inHandler);
    }

    private void init(int maxLoadingTasks, Handler inHandler) {
        this.taskExecutor = new SimpleTaskExecutor();
        this.maxLoadingTasks = maxLoadingTasks;

        if (inHandler != null) {
            handler = inHandler;
        } else {
            handlerThread = new HandlerThread("SimpleTaskManager Thread");
            handlerThread.start();
            handler = new Handler(handlerThread.getLooper());
        }
        callbackHandler = new Handler(Looper.myLooper());

        loadingTasks = new SimpleTaskPool(handler);
        loadingTasks.addListener(this);

        // TODO: consider putting it to taskProviders list
        waitingTasks = new PriorityTaskProvider(handler, "TaskManagerProviderId");
        waitingTasks.addListener(this);

        listeners = new WeakRefList<TaskManagerListener>();
        taskProviders = createTaskProvidersTreeSet();

        limits = new SparseArray<Float>();
        usedSpace = new SparseArray<Integer>();
    }

    private SafeList<TaskProvider> createTaskProvidersTreeSet() {
        SortedList<TaskProvider> sortedList = new SortedList<TaskProvider>(new Comparator<TaskProvider>() {
            @Override
            public int compare(TaskProvider lhs, TaskProvider rhs) {
                if (lhs.getPriority() == rhs.getPriority()) {
                    return 0;
                }

                if (lhs.getPriority() > rhs.getPriority()) {
                    return -1;
                }

                return 1;
            }
        });

        return new SafeList<>(sortedList, callbackHandler);
    }

    // TODO: we need run tasks when increase the size
    @Override
    public void setMaxLoadingTasks(int maxLoadingTasks) {
        checkHandlerThread();

        this.maxLoadingTasks = maxLoadingTasks;
    }

    @Override
    public int getMaxLoadingTasks() {
        checkHandlerThread();

        return maxLoadingTasks;
    }

    @Override
    public void addTask(final Task task) {
        //TODO: think how to handle adding two same task
        //Assert.assertEquals(task.getTaskStatus(), Task.Status.NotStarted);
        if (!Tasks.isTaskReadyToStart(task)) {
            Log.d(TAG, "Can't put task " + task.getClass().toString() + " because it has been added " + task.getTaskStatus().toString());
            return;
        }

        // TODO: actually I think that problem of adding the same task is not important
        // TaskManager must set Waiting status on the current thread
        task.getPrivate().setTaskStatus(Task.Status.Waiting);

        HandlerTools.runOnHandlerThread(handler, new Runnable() {
            @Override
            public void run() {
                // task will be launched in onTaskAdded method
                waitingTasks.addTask(task);
            }
        });
    }

    @Override
    public int getLoadingTaskCount() {
        return loadingTasks.getTaskCount();
    }

    @Override
    public void startImmediately(final Task task) {
        HandlerTools.runOnHandlerThread(handler, new Runnable() {
            @Override
            public void run() {
                if (handleTaskLoadPolicy(task)) {
                    final Task.Callback originalCallback = task.getTaskCallback();
                    task.setTaskCallback(new Task.Callback() {
                        @Override
                        public void onCompleted(final boolean cancelled) {
                            final Task.Callback thisCallback = this;

                            HandlerTools.runOnHandlerThread(handler, new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG, "status " + task.getTaskStatus());
                                    if (Tasks.isTaskCompleted(task)) {
                                        // may happen if canBeCancelledImmediately returns true
                                        return;
                                    }

                                    onTaskRemoved(loadingTasks, task);

                                    Task.Callback resultCallback = task.getTaskCallback() == thisCallback ? originalCallback : task.getTaskCallback();
                                    handleTaskCompletionOnThread(task, resultCallback, cancelled);
                                }
                            });
                        }
                    });

                    onTaskAdded(loadingTasks, task);

                    task.getPrivate().setTaskStatus(Task.Status.Started);
                    task.getPrivate().setTaskStartDate(new Date());

                    taskExecutor.executeTask(task, task.getTaskCallback());

                } else {
                    cancelTaskOnThread(task, null);
                }
            }
        });
    }

    @Override
    public void cancel(final Task task, final Object info) {
        HandlerTools.runOnHandlerThread(handler, new Runnable() {
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

        HandlerTools.runOnHandlerThread(handler, new Runnable() {
            @Override
            public void run() {
                addTaskProviderOnThread(provider);
            }
        });
    }

    public ArrayList<TaskProvider> getTaskProviders() {
        checkHandlerThread();

        return taskProviders;
    }

    private void addTaskProviderOnThread(TaskProvider provider) {
        checkHandlerThread();

        TaskProvider oldTaskProvider = getTaskProvider(provider.getTaskProviderId());
        if (oldTaskProvider != null) {
            taskProviders.remove(oldTaskProvider);
        }

        provider.addListener(this);
        taskProviders.add(provider);
    }

    public void setTaskProviderPriority(final TaskProvider provider, final int priority) {
        HandlerTools.runOnHandlerThread(handler, new Runnable() {
            @Override
            public void run() {
                setTaskProviderPriorityOnThread(provider, priority);
            }
        });
    }

    private void setTaskProviderPriorityOnThread(TaskProvider provider, int priority) {
        checkHandlerThread();

        provider.setPriority(priority);
        ((SortedList)taskProviders.getOriginalList()).updateSortedOrder();
    }

    public TaskProvider getTaskProvider(String id) {
        TaskProvider taskProvider = null;

        if (Looper.myLooper() == taskProviders.getHandler().getLooper()) {
            taskProvider = findProvider(taskProviders.getSafeList(), id);

        } else {
            checkHandlerThread();
            taskProvider = findProvider(this.taskProviders, id);
        }

        return taskProvider;
    }

    @Nullable
    private TaskProvider findProvider(ArrayList<TaskProvider> providers, String id) {
        for (TaskProvider taskProvider : providers) {
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

    public TaskExecutor getTaskExecutor() {
        return this.taskExecutor;
    }

    // == TaskPool.TaskPoolListener

    @Override
    public void onTaskAdded(final TaskPool pool, final Task task) {
        checkHandlerThread();

        final boolean isLoadingPool = pool == loadingTasks;
        if (isLoadingPool) {
            updateUsedSpace(task.getTaskType(), true);
        }

        Log.d("add","onTaskAdded " + isLoadingPool + " " + task.getTaskId());
        triggerOnTaskAdded(task, isLoadingPool);

        if (!isLoadingPool) {
            if (handleTaskLoadPolicy(task)) {
                checkTasksToRunOnThread();

            } else {
                cancelTaskOnThread(task, null);
            }
        }
    }

    @Override
    public void onTaskRemoved(TaskPool pool, final Task task) {
        checkHandlerThread();

        final boolean isLoadingPool = pool == loadingTasks;
        if (isLoadingPool) {
            updateUsedSpace(task.getTaskType(), false);
        }

        Log.d("add","onTaskRemoved " + isLoadingPool + " " + task.getTaskId());
        triggerOnTaskRemoved(task, isLoadingPool);
    }

    // == TaskPool interface

    @Override
    public Handler getHandler() {
        return handler;
    }

    @Override
    public void setHandler(Handler handler) {
        /*if (this.streamReader != null) {
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
        //unnecessary
    }

    @Override
    public void removeListener(TaskPoolListener listener) {
        //unnecessary
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
        HandlerTools.runOnHandlerThread(handler, new Runnable() {
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
        HandlerTools.runOnHandlerThread(handler, new Runnable() {
            @Override
            public void run() {
                if (availableQueuePart <= 0.0f) {
                    limits.remove(taskType);
                } else {
                    limits.put(taskType, availableQueuePart);
                }

                for (WeakReference<TaskManagerListener> listener : listeners) {
                    listener.get().onLimitsChanged(SimpleTaskManager.this, taskType, availableQueuePart);
                }
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

    // TODO: add getter and setter in the interface
    public void setWaitingTaskProvider(TaskProvider provider) {
        Assert.assertEquals(provider.getHandler(), handler);

        this.waitingTasks = provider;
    }

    // Private

    private boolean handleTaskLoadPolicy(Task task) {
        checkHandlerThread();

        //search for the task with the same id
        if (task.getTaskId() != null) {
            Task addedTask = loadingTasks.getTask(task.getTaskId());

            assertTrue(addedTask != task);
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
                /*if (!Tasks.isTaskReadyToStart(task)) {
                    Task testTask = takeTaskToRunOnThread();
                }*/

                assertTrue(Tasks.isTaskReadyToStart(task));
                addLoadingTaskOnThread(task);
                startTaskOnThread(task);
            }
        }
    }

    private Task takeTaskToRunOnThread() {
        checkHandlerThread();

        List<Integer> taskTypesToFilter = getTaskTypeFilter();
        Task topWaitingTask = this.waitingTasks.getTopTask(taskTypesToFilter);

        TaskProvider topTaskProvider = null;
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
                    topTaskProvider = provider;
                }
            }

            ++i;
        }

        if (topPriorityTask != null && !reachedLimit(topPriorityTask.getTaskType())) {
            if (topTaskProvider == null && topPriorityTask != null) {
                topPriorityTask = waitingTasks.takeTopTask(taskTypesToFilter);

            } else if (topTaskProvider != null) {
                topPriorityTask = topTaskProvider.takeTopTask(taskTypesToFilter);
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
        assertTrue(Tasks.isTaskReadyToStart(task));

        logTask(task, "Task started");
        task.getPrivate().setTaskStatus(Task.Status.Started);
        task.getPrivate().setTaskStartDate(new Date());

        final Task.Callback originalCallback = task.getTaskCallback();
        Task.Callback callback = new Task.Callback() {
            @Override
            public void onCompleted(final boolean cancelled) {
                final Task.Callback thisCallback = this;

                HandlerTools.runOnHandlerThread(handler, new Runnable() {
                    @Override
                    public void run() {
                        if (Tasks.isTaskCompleted(task)) {
                            // may happen if canBeCancelledImmediately returns true
                            return;
                        }

                        logTask(task, cancelled ? "Task onCompleted (Cancelled)" : "Task onCompleted");
                        Task.Callback resultCallback = task.getTaskCallback() == thisCallback ? originalCallback : task.getTaskCallback();
                        handleTaskCompletionOnThread(task, resultCallback, cancelled);
                    }
                });
            }
        };

        task.setTaskCallback(callback);
        taskExecutor.executeTask(task, callback);
    }

    private void logTask(Task task, String prefix) {
        Log.d(TAG, prefix + " " + task.getClass().toString() + "(" + task.getTaskStatus() + ")" + " id= " + task.getTaskId() + " priority= " + task.getTaskPriority() + " time " + task.getTaskDuration());
    }

    private void checkHandlerThread() {
        Assert.assertEquals(Looper.myLooper(), handler.getLooper());
    }

    public void handleTaskCompletionOnThread(final Task task, final Task.Callback callback, boolean isCancelled) {
        checkHandlerThread();

        final Task.Status status = isCancelled ? Task.Status.Cancelled : Task.Status.Finished;

        if (isCancelled) {
            task.getPrivate().setTaskError(new CancelError());
        }

        // the task will be removed from the provider automatically
        Log.d("tag", "task status " + task.getTaskStatus().toString());
        task.getPrivate().setTaskStatus(status);
        task.getPrivate().clearAllListeners();
        task.setTaskCallback(callback); // return original callback

        HandlerTools.runOnHandlerThread(callbackHandler, new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    // TODO: looks strange to pass status == ... everywhere
                    callback.onCompleted(status == Task.Status.Cancelled);
                }
            }
        });

        //TODO: for the another status like cancelled new task won't be started
        //but we cant't just call checkTasksToRunOnThread because of Task.LoadPolicy.CancelAdded
        //because we want to have new task already in waiting queue but now it isn't
        if (status == Task.Status.Finished) {
            checkTasksToRunOnThread();
        }
    }

    private void cancelTaskOnThread(Task task, final Object info) {
        checkHandlerThread();

        if (!task.getPrivate().getNeedCancelTask()) {
            Task.Status st = task.getTaskStatus();
            task.getPrivate().cancelTask(info);

            if (st == Task.Status.Waiting || st == Task.Status.NotStarted || st == Task.Status.Blocked || task.getPrivate().canBeCancelledImmediately()) {
                if (st == Task.Status.Started) {
                    task.getStartCallback().onCompleted(true); // to get the original callback

                } else {
                    handleTaskCompletionOnThread(task, task.getTaskCallback(), true);
                    logTask(task, "Cancelled");
                }
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
            assertTrue(count > 0);
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
