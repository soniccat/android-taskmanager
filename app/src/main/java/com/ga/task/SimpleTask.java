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

public abstract class SimpleTask implements Task {

    final protected TaskImpl impl = new TaskImpl(this);

    public SimpleTask() {
        super();
    }

    @Override
    public Callback getTaskCallback() {
        return getPrivate().getTaskCallback();
    }

    @Override
    public void setTaskCallback(Callback callback) {
        getPrivate().setTaskCallback(callback);
    }

    @Override
    public Task.Status getTaskStatus() {
        return getPrivate().getTaskStatus();
    }

    @Override
    public Error getTaskError() {
        return getPrivate().getTaskError();
    }

    @Override
    public Object getTaskUserData() {
        return getPrivate().getTaskUserData();
    }

    @Override
    public void setTaskUserData(Object object) {
        getPrivate().setTaskUserData(object);
    }

    @Override
    public void setTaskPriority(int value) {
        getPrivate().setTaskPriority(value);
    }

    @Override
    public int getTaskPriority() {
        return getPrivate().getTaskPriority();
    }

    @Override
    public void setTaskId(String id) {
        getPrivate().setTaskId(id);
    }

    @Override
    public String getTaskId() {
        return getPrivate().getTaskId();
    }

    @Override
    public void setLoadPolicy(LoadPolicy loadPolicy) {
        getPrivate().setLoadPolicy(loadPolicy);
    }

    @Override
    public LoadPolicy getLoadPolicy() {
        return getPrivate().getLoadPolicy();
    }

    @Override
    public Task getTask() {
        return this;
    }

    @Override
    public void setTaskProgressMinChange(float value) {
        getPrivate().setTaskProgressMinChange(value);
    }

    @Override
    public float getTaskProgressMinChange() {
        return getPrivate().getTaskProgressMinChange();
    }

    @Override
    public Object getCancellationInfo() {
        return getPrivate().getCancellationInfo();
    }

    @Override
    public void setTaskType(int type) {
        getPrivate().setTaskType(type);
    }

    @Override
    public int getTaskType() {
        return getPrivate().getTaskType();
    }

    @Override
    public void addTaskStatusListener(StatusListener listener) {
        getPrivate().addTaskStatusListener(listener);
    }

    @Override
    public void removeTaskStatusListener(StatusListener listener) {
        getPrivate().removeTaskStatusListener(listener);
    }

    @Override
    public void addTaskProgressListener(ProgressListener listener) {
        getPrivate().addTaskProgressListener(listener);
    }

    @Override
    public void removeTaskProgressListener(ProgressListener listener) {
        getPrivate().removeTaskProgressListener(listener);
    }

    @Override
    public long getTaskDuration() {
        return getPrivate().getTaskDuration();
    }

    @Override
    public void addTaskDependency(Task task) {
        getPrivate().addTaskDependency(task);
    }

    @Override
    public void removeTaskDependency(Task task) {
        getPrivate().removeTaskDependency(task);
    }

    @Override
    public TaskPrivate getPrivate() {
        return impl;
    }
}
