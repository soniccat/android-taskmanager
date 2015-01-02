package com.ga.task;

import android.os.Handler;

/**
 * Created by alexeyglushkov on 30.12.14.
 */

// A provider defines in which order tasks should be executed
// A provider must remove a task from the pool in the takeTopTask method

public interface TaskProvider {
    void setTaskPool(TaskPool pool);
    TaskPool getTaskPool();

    void setHandler(Handler handler);

    Task getTopTask();
    Task takeTopTask();
}
