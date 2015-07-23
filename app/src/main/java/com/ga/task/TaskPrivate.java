package com.ga.task;

import java.util.Date;

/**
 * Created by alexeyglushkov on 21.07.15.
 */

// Contains methods being used only by TaskManager and TaskPool

public interface TaskPrivate extends Task{

    // Start the task
    //
    // Caller: TaskManager
    //
    void startTask();

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

    // Set Date after changing the state to Started
    //
    // Caller: TaskManager
    //
    void setTaskFinishDate(Date date);

    // Clear all progress and status listeners
    //
    // Caller: TaskManager
    //
    void clearAllListeners();
}
