package com.example.alexeyglushkov.taskmanager.task;

import android.os.Looper;
import android.util.Log;

import com.example.alexeyglushkov.streamlib.progress.ProgressInfo;
import com.example.alexeyglushkov.streamlib.progress.ProgressListener;
import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;
import com.example.alexeyglushkov.tools.HandlerTools;

import junit.framework.Assert;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by alexeyglushkov on 23.07.15.
 */
public abstract class TaskImpl implements Task, TaskPrivate {
    protected Task.Callback callback;
    protected Task.Callback startCallback;
    protected Object cancellationInfo;
    protected Task.Status taskStatus = Task.Status.NotStarted;
    protected Object taskUserData;
    protected boolean needCancelTask;
    protected boolean isCancelled; // is set in subclasses when task is really cancelled
    protected float progressMinChange = 0.1f;
    protected Error error;
    protected Object result;
    protected String taskId;
    protected LoadPolicy loadPolicy = LoadPolicy.SkipIfAdded;
    protected int priority;
    protected int type;

    protected Date startDate;
    protected Date finishDate;

    protected WeakRefList<Task> blockedByTasks;

    // listeners are cleared in a TaskManager after task finishing or cancelling
    protected ArrayList<StatusListener> statusListeners;
    protected WeakRefList<ProgressListener> progressListeners;

    public TaskImpl() {
        super();

        blockedByTasks = new WeakRefList<Task>();
        statusListeners = new ArrayList<StatusListener>();
        progressListeners = new WeakRefList<>();
    }

    @Override
    public Callback getTaskCallback() {
        return this.callback;
    }

    @Override
    public Callback getStartCallback() {
        return startCallback;
    }

    @Override
    public void setTaskCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void cancelTask(Object info) {
        needCancelTask = true;
        this.cancellationInfo = info;
    }

    @Override
    public void setTaskStatus(Task.Status status) {
        Task.Status oldStatus = this.taskStatus;
        this.taskStatus = status;

        Log.d("TaskImpl", "setTaskStatus " + status + " " + Thread.currentThread()); //TODO: CRASH: get concurrent access crash in triggerStatusListeners
        triggerStatusListeners(oldStatus, this.taskStatus);
    }

    @Override
    public Task.Status getTaskStatus() {
        return this.taskStatus;
    }

    public void setTaskError(Error error) {
        this.error = error;
    }

    @Override
    public Error getTaskError() {
        return error;
    }

    @Override
    public Object getTaskUserData() {
        return taskUserData;
    }

    @Override
    public void setTaskUserData(Object object) {
        this.taskUserData = object;
    }

    @Override
    public void setTaskPriority(int value) {
        priority = value;
    }

    @Override
    public int getTaskPriority() {
        return priority;
    }

    @Override
    public void setTaskId(String id) {
        this.taskId = id;
    }

    @Override
    public String getTaskId() {
        return this.taskId;
    }

    @Override
    public void setLoadPolicy(LoadPolicy loadPolicy) {
        this.loadPolicy = loadPolicy;
    }

    @Override
    public LoadPolicy getLoadPolicy() {
        return this.loadPolicy;
    }

    @Override
    public Task getTask() {
        return this;
    }

    @Override
    public void setTaskProgressMinChange(float value) {
        progressMinChange = value;
    }

    @Override
    public float getTaskProgressMinChange() {
        return progressMinChange;
    }

    @Override
    public Object getCancellationInfo() {
        return cancellationInfo;
    }

    @Override
    public void setTaskType(int type) {
        this.type = type;
    }

    @Override
    public int getTaskType() {
        return type;
    }

    @Override
    public void startTask(Callback callback) {
        getPrivate().handleTaskStart(callback);
    }

    @Override
    public boolean getNeedCancelTask() {
        return needCancelTask;
    }

    public Object getTaskResult() {
        return result;
    }

    public void setTaskResult(Object result) {
        this.result = result;
    }

    @Override
    public void addTaskStatusListener(StatusListener listener) {
        statusListeners.add(listener);
    }

    @Override
    public void removeTaskStatusListener(StatusListener listener) {
        for (int i=0; i<statusListeners.size(); ++i) {
            if (statusListeners.get(i) == listener) {
                statusListeners.remove(i);
                break;
            }
        }
    }

    @Override
    public void addTaskProgressListener(ProgressListener listener) {
        checkMainThread();

        progressListeners.add(new WeakReference<ProgressListener>(listener));
    }

    @Override
    public void removeTaskProgressListener(ProgressListener listener) {
        checkMainThread();

        int i=0;
        for (WeakReference<ProgressListener> l : progressListeners) {
            if (l.get() == listener) {
                progressListeners.remove(i);
                break;
            }

            ++i;
        }
    }

    @Override
    public void clearAllListeners() {
        statusListeners.clear();

        if (progressListeners.size() > 0) {
            HandlerTools.runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    progressListeners.clear();
                }
            });
        }
    }

    @Override
    public long getTaskDuration() {
        if (startDate != null) {
            long finishTime = finishDate == null ? new Date().getTime() : finishDate.getTime();
            return finishTime - startDate.getTime();
        }
        return -1;
    }

    @Override
    public void setTaskStartDate(Date date) {
        this.startDate = date;
    }

    @Override
    public void setTaskFinishDate(Date date) {
        this.finishDate = date;
    }

    //TODO: add dependencies support

    @Override
    public void addTaskDependency(Task task) {
        blockedByTasks.add(new WeakReference<Task>(task));
    }

    @Override
    public void removeTaskDependency(Task task) {
        blockedByTasks.remove(task);
    }

    public WeakRefList<Task> getDependencies() {
        return blockedByTasks;
    }

    public boolean isBlocked() {
        boolean isBlocked = false;
        for (WeakReference<Task> task : blockedByTasks) {
            isBlocked = !Tasks.isTaskCompleted(task.get());
            if (isBlocked) {
                break;
            }
        }

        return isBlocked;
    }

    @Override
    public void clear() {
        Assert.assertTrue(Tasks.isTaskCompleted(this));

        taskStatus = Status.NotStarted;
        cancellationInfo = null;
        needCancelTask = false;
        isCancelled = false;
        error = null;
        result = null;
        startDate = null;
        finishDate = null;
    }

    @Override
    public TaskPrivate getPrivate() {
        return this;
    }

    @Override
    public ProgressUpdater createProgressUpdater(float contentSize) {
        ProgressUpdater updater = new ProgressUpdater(contentSize, getTaskProgressMinChange(), new ProgressUpdater.ProgressUpdaterListener() {
            @Override
            public void onProgressUpdated(ProgressUpdater updater) {
                triggerProgressListeners(updater);
            }

            @Override
            public void onProgressCancelled(ProgressUpdater updater, Object info) {
                triggerProgressListeners(updater);
                cancelTask(info);
            }
        });
        return updater;
    }

    private void triggerStatusListeners(Task.Status oldStatus, Task.Status newStatus) {
        if (oldStatus != newStatus) {
            for (StatusListener l : statusListeners) {
                if (l != null) {
                    l.onTaskStatusChanged(TaskImpl.this, oldStatus, newStatus);
                }
            }
        }
    }

    public void triggerProgressListeners(final ProgressInfo progressInfo) {
        if (progressListeners.size() > 0) {
            HandlerTools.runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    for (WeakReference<ProgressListener> l : progressListeners) {
                        l.get().onProgressChanged(TaskImpl.this, progressInfo);
                    }
                }
            });
        }
    }

    protected void setIsCancelled() {
        isCancelled = true;
    }

    @Override
    public boolean canBeCancelledImmediately() {
        return false;
    }

    @Override
    public void handleTaskStart(Callback callback) {
        startCallback = callback;
    }

    @Override
    public void handleTaskCompletion(Callback callback) {
        if (callback != null) {
            callback.onCompleted(isCancelled);
        }
    }

    private void checkMainThread() {
        Assert.assertEquals(Looper.getMainLooper().getThread(), Thread.currentThread());
    }
}
