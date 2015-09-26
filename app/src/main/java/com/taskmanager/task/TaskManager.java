package com.taskmanager.task;

import android.util.SparseArray;

/**
 * Created by alexeyglushkov on 20.09.14.
 */

public interface TaskManager extends TaskPool {
    // Task Limits
    void setMaxLoadingTasks(int maxLoadingTasks);
    int getMaxLoadingTasks();

    void setLimit(int taskType, float availableQueuePart);
    SparseArray<Float> getLimits();
    SparseArray<Integer> getUsedSpace(); //type -> task count from loadingTasks



    // Task Running

    // Put a task, the same task can't be putted twice to the TaskManager
    // TODO: handle this situation well
    void startImmediately(Task task);
    void cancel(Task task, Object info);
    int getLoadingTaskCount();

    // Task Providers
    void addTaskProvider(TaskProvider provider);
    void removeTaskProvider(TaskProvider provider);
    SortedList<TaskProvider> getTaskProviders(); //always sorted by priority

    TaskProvider getTaskProvider(String id);
    void setTaskProviderPriority(TaskProvider provider, int priority);

    // Executor
    void setTaskExecutor(TaskExecutor executor);
    TaskExecutor getTaskExecutor();

    // Listeners
    void removeListener(TaskManagerListener listener);
    void addListener(TaskManagerListener listener);

    interface TaskManagerListener {
        void onLimitsChanged(TaskManager taskManager, int taskType, float availableQueuePart);

        // add additional flag showing that a task added to a loading or waiting queue
        void onTaskAdded(TaskPool pool, Task task, boolean isLoadingQueue);
        void onTaskRemoved(TaskPool pool, Task task, boolean isLoadingQueue);
    }
}
