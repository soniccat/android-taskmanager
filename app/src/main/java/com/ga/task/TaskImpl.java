package com.ga.task;

import android.os.Looper;

import com.ga.loader.ProgressInfo;
import com.ga.loader.ProgressUpdater;
import com.rssclient.controllers.Tools;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by alexeyglushkov on 23.07.15.
 */
public class TaskImpl implements Task, TaskPrivate {
    private Task outerTask;

    protected Task.Callback startCallback;
    protected Object cancellationInfo;
    protected Task.Status taskStatus = Task.Status.NotStarted;
    protected Object taskUserData;
    protected boolean needCancelTask;
    protected boolean isCancelled;
    protected float progressMinChange;
    protected Error error;
    protected String taskId;
    protected LoadPolicy loadPolicy = LoadPolicy.SkipIfAdded;
    protected int priority;
    protected int type;

    protected Date startDate;
    protected Date finishDate;

    // listeners are cleared in a TaskManager after task finishing or cancelling
    protected ArrayList<StatusListener> statusListeners;
    protected ArrayList<ProgressListener> progressListeners;

    public TaskImpl(Task outerTask) {
        super();

        this.outerTask = outerTask;
        statusListeners = new ArrayList<StatusListener>();
        progressListeners = new ArrayList<ProgressListener>();
    }

    @Override
    public void startTask() {
        // Doesn't implement
    }

    @Override
    public Callback getTaskCallback() {
        return this.startCallback;
    }

    @Override
    public void setTaskCallback(Callback callback) {
        this.startCallback = callback;
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
    public boolean getNeedCancelTask() {
        return needCancelTask;
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

        progressListeners.add(listener);
    }

    @Override
    public void removeTaskProgressListener(ProgressListener listener) {
        checkMainThread();

        int i=0;
        for (ProgressListener l : progressListeners) {
            if (l == listener) {
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
            Tools.postOnMainLoop(new Runnable() {
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

    }

    @Override
    public void removeTaskDependency(Task task) {

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
        });
        return updater;
    }

    private void triggerStatusListeners(Task.Status oldStatus, Task.Status newStatus) {
        if (oldStatus != newStatus) {
            for (StatusListener l : statusListeners) {
                if (l != null) {
                    l.onTaskStatusChanged(outerTask, oldStatus, newStatus);
                }
            }
        }
    }

    private void triggerProgressListeners(final ProgressInfo progressInfo) {
        if (progressListeners.size() > 0) {
            Tools.postOnMainLoop(new Runnable() {
                @Override
                public void run() {
                    for (ProgressListener l : progressListeners) {
                        if (l != null) {
                            l.onTaskProgressChanged(outerTask, progressInfo);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void handleTaskCompletion() {
        callStartCallback();
    }

    private void callStartCallback() {
        if (startCallback != null) {
            startCallback.finished(isCancelled);
            startCallback = null;
        }
    }

    private void checkMainThread() {
        Assert.assertEquals(Looper.getMainLooper().getThread(), Thread.currentThread());
    }
}
