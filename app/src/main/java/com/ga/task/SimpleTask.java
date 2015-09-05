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
        return impl.getTaskCallback();
    }

    @Override
    public void setTaskCallback(Callback callback) {
        impl.setTaskCallback(callback);
    }

    @Override
    public Task.Status getTaskStatus() {
        return impl.getTaskStatus();
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
    public void setTaskProgressMinChange(float value) {
        impl.setTaskProgressMinChange(value);
    }

    @Override
    public float getTaskProgressMinChange() {
        return impl.getTaskProgressMinChange();
    }

    @Override
    public Object getCancellationInfo() {
        return impl.getCancellationInfo();
    }

    @Override
    public void setTaskType(int type) {
        impl.setTaskType(type);
    }

    @Override
    public int getTaskType() {
        return impl.getTaskType();
    }

    @Override
    public void addTaskStatusListener(StatusListener listener) {
        impl.addTaskStatusListener(listener);
    }

    @Override
    public void removeTaskStatusListener(StatusListener listener) {
        impl.removeTaskStatusListener(listener);
    }

    @Override
    public void addTaskProgressListener(ProgressListener listener) {
        impl.addTaskProgressListener(listener);
    }

    @Override
    public void removeTaskProgressListener(ProgressListener listener) {
        impl.removeTaskProgressListener(listener);
    }

    @Override
    public long getTaskDuration() {
        return impl.getTaskDuration();
    }

    @Override
    public void addTaskDependency(Task task) {
        impl.addTaskDependency(task);
    }

    @Override
    public void removeTaskDependency(Task task) {
        impl.removeTaskDependency(task);
    }

    @Override
    public TaskPrivate getPrivate() {
        return impl;
    }
}
