package com.ga.task;

/**
 * Created by alexeyglushkov on 27.12.14.
 */
public interface ModelTask {

    // Notify that state of the model object is changed
    // here you should change your isLoading flag
    // Finished state will be set before the callback of the task
    //
    // Caller: TaskManager
    // Thread: Main thread
    void modelTaskStateChangeHandle(Task.Status oldStatus, Task.Status newStatus);


}
