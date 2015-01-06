package com.ga.task;

import android.os.Handler;
import android.util.SparseArray;

import java.util.List;

/**
 * Created by alexeyglushkov on 20.09.14.
 */

// TODO: think about implementing TaskPool
public interface TaskManager {
    Handler getHandler();

    // Put a task, the same task can't be putted twice to the TaskManager
    void put(Task task);
    void startImmediately(Task task);
    void cancel(Task task, Object info);
    void addTaskProvider(TaskProvider provider);
    void setLimit(int taskType, float availableQueuePart);
    TaskManagerSnapshot createSnapshot();

    //Store information to show it outside for debugging purpose
    public interface TaskManagerSnapshot {
        int getLoadingTasksCount();
        int getWaitingTasksCount();
        int getMaxQueueSize();
        SparseArray<Float> getLoadingLimits();
        SparseArray<Integer> getUsedLoadingSpace();
        SparseArray<Integer> getWaitingTaskInfo();
    }
}
