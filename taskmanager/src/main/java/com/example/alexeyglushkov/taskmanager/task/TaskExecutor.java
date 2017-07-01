package com.example.alexeyglushkov.taskmanager.task;

/**
 * Created by alexeyglushkov on 08.02.15.
 */
public interface TaskExecutor {
    void executeTask(Task task, Task.Callback callback);
}
