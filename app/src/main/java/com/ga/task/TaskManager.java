package com.ga.task;

import android.os.Handler;

import java.util.List;

/**
 * Created by alexeyglushkov on 20.09.14.
 */

// TODO: think about implementing TaskPool
public interface TaskManager {
    Handler getHandler();

    // Put a task, the same task can't be putted twice to the TaskManager
    void put(Task task);
    void cancel(Task task, Object info);
    void addTaskProvider(TaskProvider provider);
}
