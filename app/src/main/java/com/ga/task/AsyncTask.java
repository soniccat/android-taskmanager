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
public abstract class AsyncTask extends android.os.AsyncTask<Void, Void, Void> implements Task {

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

    protected ArrayList<WeakReference<StatusListener>> statusListeners;
    protected ArrayList<WeakReference<ProgressListener>> progressListeners;

    public AsyncTask() {
        super();

        statusListeners = new ArrayList<WeakReference<StatusListener>>();
        progressListeners = new ArrayList<WeakReference<ProgressListener>>();
    }

    @Override
    public void startTask() {
        this.execute();
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
            statusListeners.add(new WeakReference<StatusListener>(listener));
        }
    }

    @Override
    public void removeTaskStatusListener(StatusListener listener) {
        synchronized (statusListeners) {
            int i = 0;
            for (WeakReference<StatusListener> l : statusListeners) {
                if (l.get() == listener) {
                    statusListeners.remove(i);
                    break;
                }

                ++i;
            }
        }
    }

    @Override
    public void addTaskProgressListener(ProgressListener listener) {
        progressListeners.add(new WeakReference<ProgressListener>(listener));
    }

    @Override
    public void removeTaskProgressListener(ProgressListener listener) {
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

    //TODO: add dependencies support

    @Override
    public void addTaskDependency(Task task) {

    }

    @Override
    public void removeTaskDependency(Task task) {

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
                ArrayList<WeakReference<StatusListener>> emptyReferences = new ArrayList<WeakReference<StatusListener>>();

                for (WeakReference<StatusListener> l : statusListeners) {
                    if (l.get() != null) {
                        l.get().onTaskStatusChanged(this, oldStatus, newStatus);
                    } else {
                        emptyReferences.add(l);
                    }
                }

                statusListeners.removeAll(emptyReferences);
            }
        }
    }

    public void triggerProgressListeners(final ProgressInfo progressInfo) {
        if (progressListeners.size() > 0) {
            Tools.postOnMainLoop(new Runnable() {
                @Override
                public void run() {
                    ArrayList<WeakReference<ProgressListener>> emptyReferences = new ArrayList<WeakReference<ProgressListener>>();

                    for (WeakReference<ProgressListener> l : progressListeners) {
                        if (l.get() != null) {
                            l.get().onTaskProgressChanged(AsyncTask.this, progressInfo);
                        } else {
                            emptyReferences.add(l);
                        }
                    }

                    progressListeners.removeAll(emptyReferences);
                }
            });
        }
    }
}
