package com.ga.task;

import android.os.Handler;
import android.util.SparseArray;

import java.util.List;

/**
 * Created by alexeyglushkov on 30.12.14.
 */

// A provider defines in which order tasks should be executed
// A provider must remove a task from the pool in the takeTopTask method

public interface TaskProvider {
    Task getTopTask(List<Integer> typesToFilter);
    Task takeTopTask(List<Integer> typesToFilter);

    void setTaskPool(TaskPool pool);
    TaskPool getTaskPool();

    void setHandler(Handler handler);
    Handler getHandler();

    void setUserData(Object data);
    Object getUserData();

    void addListener(TaskProviderListener listener);
    void removeListener(TaskProviderListener listener);

    public interface TaskProviderListener {
        void onTaskAdded(TaskProvider provider, Task task);
        void onTaskRemoved(TaskProvider provider, Task task);
    }
}
