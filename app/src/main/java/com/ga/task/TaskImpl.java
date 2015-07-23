package com.ga.task;

import com.ga.loader.ProgressInfo;
import com.ga.loader.ProgressUpdater;
import com.rssclient.controllers.*;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by alexeyglushkov on 23.07.15.
 */
public class TaskImpl implements Task, TaskPrivate {
    protected Task.Callback startCallback;
    protected Object cancellationInfo;
    protected Task.Status taskStatus = Task.Status.NotStarted;
    protected Object taskUserData;
    protected boolean needCancelTask;
    protected boolean isCancelled;
    protected float progress;
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

    public TaskImpl() {
        super();

        statusListeners = new ArrayList<StatusListener>();
        progressListeners = new ArrayList<ProgressListener>();
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
    public float getTaskProgress() {
        return progress;
    }

    @Override
    public void setTaskProgressMinChange(float value) {
        progressMinChange = value;
    }

    public void setTaskProgress(float value) {
        this.progress = value;
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
        synchronized(statusListeners) {
            statusListeners.add(listener);
        }
    }

    @Override
    public void removeTaskStatusListener(StatusListener listener) {
        synchronized (statusListeners) {
            int i = 0;
            for (StatusListener l : statusListeners) {
                if (l == listener) {
                    statusListeners.remove(i);
                    break;
                }

                ++i;
            }
        }
    }

    @Override
    public void addTaskProgressListener(ProgressListener listener) {
        progressListeners.add(listener);
    }

    @Override
    public void removeTaskProgressListener(ProgressListener listener) {
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
        progressListeners.clear();
    }

    @Override
    public long getTaskDuration() {
        if (startDate != null) {
            return new Date().getTime() - startDate.getTime();
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
}
