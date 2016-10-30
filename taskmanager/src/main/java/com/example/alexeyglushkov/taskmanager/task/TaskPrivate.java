package com.example.alexeyglushkov.taskmanager.task;

import com.example.alexeyglushkov.streamlib.progress.ProgressInfo;
import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;

import java.util.Date;

/**
 * Created by alexeyglushkov on 21.07.15.
 */

// Contains methods being used only by TaskManager, TaskPool or in a Task

public interface TaskPrivate extends Task {

    // Marks the task that it should be cancelled
    // it will have Cancelled state if the task is started and successfully cancelled
    // if it isn't possible to cancel the task the state will be set to Finished
    // otherwise NotStarted state will be set
    //
    // Caller: TaskManager
    //
    void cancelTask(Object info);
    boolean getNeedCancelTask();

    // Set current state of the task
    //
    // Caller: Client, TaskManager
    //
    void setTaskStatus(Status status);

    // Set Date after changing the state to Started
    //
    // Caller: TaskManager
    //
    void setTaskStartDate(Date date);

    // Set error
    //
    // Caller: Task
    void setTaskError(Error error);

    // TODO: need to call
    // Set Date after changing the state to Started
    //
    // Caller: TaskManager
    //
    void setTaskFinishDate(Date date);

    // Get dependencies
    //
    // Caller: TaskManager
    //
    WeakRefList<Task> getDependencies();

    // Clear all progress and status listeners
    //
    // Caller: TaskManager
    //
    void clearAllListeners();

    // Create a ProgressUpdater which trigger progress listeners on changes
    // Generally used to pass in a reader object
    //
    // Caller: Task
    //
    ProgressUpdater createProgressUpdater(float contentSize);

    // Notify all progress listeners
    //
    // Caller: Task
    //
    void triggerProgressListeners(final ProgressInfo progressInfo);

    // Call the start callback
    //
    // Caller: Task
    //
    void handleTaskCompletion();

    // To keep the result
    //
    // Caller: Task
    //
    void setTaskResult(Object result);

    // To reset loaded data and states to load it again
    //
    // Caller:
    //
    void clear();

    // Flag shows that the command could be removed from the queue before finishing
    // It make sense to keep it false for a task that can affect performance if it is being cancelled
    //
    // Caller: TaskManager
    //
    boolean canBeCancelledImmediately();
}
