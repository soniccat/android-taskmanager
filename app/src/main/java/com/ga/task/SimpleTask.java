package com.ga.task;

import com.ga.loader.ProgressInfo;
import com.ga.loader.ProgressUpdater;
import com.rssclient.controllers.Tools;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by alexeyglushkov on 20.09.14.
 */

//TODO: detach from AcyncTask to a separate TaskLauncher class/interface
public abstract class SimpleTask implements Task {

    TaskImpl impl;

    // listeners are cleared in a TaskManager after task finishing or cancelling
    protected ArrayList<StatusListener> statusListeners;
    protected ArrayList<ProgressListener> progressListeners;

    public SimpleTask() {
        super();

        statusListeners = new ArrayList<StatusListener>();
        progressListeners = new ArrayList<ProgressListener>();
    }

    @Override
    public Callback getTaskCallback() {
        return impl.getTaskCallback()
    }

    @Override
    public void setTaskCallback(Callback callback) {
        impl.setTaskCallback(callback);
    }

    @Override
    public Task.Status getTaskStatus() {
        return impl.getTaskStatus()
    }

    public void setTaskError(Error error) {
        impl.setTaskError(error);
    }

    @Override
    public Error getTaskError() {
        return impl.getTaskError();
    }

    @Override
    public Object getTaskUserData() {
        return impl.getTaskUserData();
    }

    @Override
    public void setTaskUserData(Object object) {
        impl.setTaskUserData(object);
    }

    @Override
    public void setTaskPriority(int value) {
        impl.setTaskPriority(value);
    }

    @Override
    public int getTaskPriority() {
        return impl.getTaskPriority();
    }

    @Override
    public void setTaskId(String id) {
        impl.setTaskId(id);
    }

    @Override
    public String getTaskId() {
        return impl.getTaskId();
    }

    @Override
    public void setLoadPolicy(LoadPolicy loadPolicy) {
        impl.setLoadPolicy(loadPolicy);
    }

    @Override
    public LoadPolicy getLoadPolicy() {
        return impl.getLoadPolicy();
    }

    @Override
    public Task getTask() {
        return this;
    }

    @Override
    public float getTaskProgress() {
        return impl.getTaskProgress();
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

    // useful methods

    protected ProgressUpdater createProgressUpdater(float contentSize) {
        ProgressUpdater updater = new ProgressUpdater(contentSize, progressMinChange, new ProgressUpdater.ProgressUpdaterListener() {
            @Override
            public void onProgressUpdated(ProgressUpdater updater) {
                triggerProgressListeners(updater);
            }
        });
        return updater;
    }

    public void callStartCallback() {
        if (startCallback != null) {
            startCallback.finished(isCancelled);
            startCallback = null;
        }
    }

    public void handleTaskCompletion() {
        callStartCallback();
    }

    public void triggerStatusListeners(Task.Status oldStatus, Task.Status newStatus) {
        if (oldStatus != newStatus) {
            synchronized (statusListeners) {
                for (StatusListener l : statusListeners) {
                    if (l != null) {
                        l.onTaskStatusChanged(this, oldStatus, newStatus);
                    }
                }
            }
        }
    }

    public void triggerProgressListeners(final ProgressInfo progressInfo) {
        if (progressListeners.size() > 0) {
            Tools.postOnMainLoop(new Runnable() {
                @Override
                public void run() {
                    for (ProgressListener l : progressListeners) {
                        if (l != null) {
                            l.onTaskProgressChanged(SimpleTask.this, progressInfo);
                        }
                    }
                }
            });
        }
    }
}
