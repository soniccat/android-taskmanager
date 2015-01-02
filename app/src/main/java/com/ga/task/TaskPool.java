package com.ga.task;

import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexeyglushkov on 30.12.14.
 */

// A storage for tasks you use in a fragment or in an activity
// A pool must remove task when it finishes

public interface TaskPool {
    void setHandler(Handler handler);

    void addTask(Task task);
    void removeTask(Task task);
    Task getTask(String taskId);
    int getTaskCount();
    List<Task> getTasks();

    void addListener(TaskPoolListener listener);
    void removeListener(TaskPoolListener listener);

    public interface TaskPoolListener {
        void onTaskAdded(Task task);
        void onTaskRemoved(Task task);
    }
}
