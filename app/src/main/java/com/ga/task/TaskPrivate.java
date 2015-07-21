package com.ga.task;

import java.util.Date;

/**
 * Created by alexeyglushkov on 21.07.15.
 */

// Contains methods being used only by TaskManager

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
    // Caller: Client's code, TaskManager
    //
    void setTaskStatus(Status status);

    // Set Date after changing the state to Started
    //
    // Caller: Client's code, TaskManager
    //
    void setTaskStartDate(Date date);
}
