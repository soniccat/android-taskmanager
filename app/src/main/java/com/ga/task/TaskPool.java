package com.ga.task;

import android.os.Handler;

import java.util.List;

/**
 * Created by alexeyglushkov on 30.12.14.
 */

// A storage for tasks you use in a fragment or in an activity
// A pool must remove task when it finishes

public interface TaskPool extends Task.StatusListener {
    void setHandler(Handler handler);
    Handler getHandler();

    void addTask(Task task);
    void removeTask(Task task);

    Task getTask(String taskId);
    int getTaskCount();
    List<Task> getTasks();


    void setUserData(Object data);
    Object getUserData();

    void addListener(TaskPoolListener listener);
    void removeListener(TaskPoolListener listener);

    interface TaskPoolListener {
        void onTaskAdded(TaskPool pool, Task task);
        void onTaskRemoved(TaskPool pool, Task task);
    }
}
