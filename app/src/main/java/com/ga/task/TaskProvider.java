package com.ga.task;

import android.os.Handler;
import android.util.SparseArray;

import java.util.List;

/**
 * Created by alexeyglushkov on 30.12.14.
 */

// A provider defines in which order tasks should be executed
// A provider must remove a task from the pool in the takeTopTask method

public interface TaskProvider extends TaskPool {
    void setTaskProviderId(String id);
    String getTaskProviderId();

    // TODO: implement it
    // priority is used to determine the order of accessing to the providers int a TaskManager
    // it affects tasks order if the tasks have the same priority
    void setPriority(int priority);
    int getPriority();

    // TODO: replace to TaskFilter
    Task getTopTask(List<Integer> typesToFilter);
    Task takeTopTask(List<Integer> typesToFilter);
}
