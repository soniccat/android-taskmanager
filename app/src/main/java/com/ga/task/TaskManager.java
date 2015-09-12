package com.ga.task;

import android.os.Handler;
import android.util.SparseArray;

import java.util.List;

/**
 * Created by alexeyglushkov on 20.09.14.
 */

public interface TaskManager extends TaskPool {
    void setMaxLoadingTasks(int maxLoadingTasks);
    int getMaxLoadingTasks();

    // Put a task, the same task can't be putted twice to the TaskManager
    void startImmediately(Task task);
    void cancel(Task task, Object info);
    int getLoadingTaskCount();

    void addTaskProvider(TaskProvider provider);
    void removeTaskProvider(TaskProvider provider);
    List<TaskProvider> getTaskProviders(); //always sorted by priority

    TaskProvider getTaskProvider(String id);
    void setTaskProviderPriority(TaskProvider provider, int priority);

    void setLimit(int taskType, float availableQueuePart);
    SparseArray<Float> getLimits();
    SparseArray<Integer> getUsedSpace(); //type -> task count from loadingTasks

    void setTaskExecutor(TaskExecutor executor);
    TaskExecutor getTaskExecutor();

    void removeListener(TaskManagerListener listener);
    void addListener(TaskManagerListener listener);

    interface TaskManagerListener {
        void onLimitsChanged(TaskManager taskManager, int taskType, float availableQueuePart);

        // add additional flag showing that a task added to a loading or waiting queue
        void onTaskAdded(TaskPool pool, Task task, boolean isLoadingQueue);
        void onTaskRemoved(TaskPool pool, Task task, boolean isLoadingQueue);
    }
}
