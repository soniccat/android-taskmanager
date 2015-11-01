package com.example.alexeyglushkov.taskmanager.task;

import com.example.alexeyglushkov.streamlib.progress.ProgressInfo;

/**
 * Created by alexeyglushkov on 20.09.14.
 */

// A task represents an action that takes time to finish. It can be downloading data, processing
// information, work with a database and other.
// The task can be started only once. If you want to repeat it you should create new one.
//
// If you work with a model on main thread the task shouldn't work with the model data stored
// outside the task. The model data should be changed on main thread after task completion and
// notify UI.
//
// All setters mustn't be called after starting the task

public interface Task extends TaskContainer {

    // Set the callback to handle the result.
    // TaskManager uses this callback but calls the original
    //
    // Caller: Client, TaskManager
    //
    void setTaskCallback(Callback callback);
    Callback getTaskCallback();

    //TODO: add custom thread callback support through set/get

    // Return info passed in the cancel method
    //
    // Caller: Client
    //
    Object getCancellationInfo();

    // Get the current Status
    //
    // Caller: Client, TaskManager, TaskPool
    //
    Status getTaskStatus();

    // Set/Get current type of the task
    // The type is used in TaskManager to be able to set load limits on a particular type
    //
    // Caller: Client, TaskManager (getter only), TaskProvider (getter only)
    //
    void setTaskType(int type);
    int getTaskType();

    // Set/Get the task's id. Tasks with the same id means that they download the same data for the same caller.
    // It's useful when you want to bind the task to a specific model object and provide an additional load policy
    // The right load policy can prevent needless loadings
    // nil means that the task doesn't have the id
    //
    // Caller: Client, TaskManager (getter only), TaskPool (getter only)
    //
    void setTaskId(String id);
    String getTaskId();

    // For tasks with the same id TaskManager uses a load policy to handle the situation when they
    // want to load data simultaneously
    //
    // Caller: Client, TaskManager (getter only), TaskPool (getter only)
    //
    void setLoadPolicy(LoadPolicy loadPolicy);
    LoadPolicy getLoadPolicy();

    // Return the error of the task happened during the execution
    // Basically it should be called from the start callback
    //
    // Caller: Client
    //
    Error getTaskError();

    // Set/Get the priority of the task
    //
    // Caller: Client, TaskProvider (getter only), TaskManager (getter only)
    //
    void setTaskPriority(int value);
    int getTaskPriority();

    // Set minimal progress change to trigger the ProgressListener
    //
    // Caller: Client
    //
    void setTaskProgressMinChange(float value);
    float getTaskProgressMinChange();

    // TODO: now it works wrong
    // Get time passed between Started and Finished/Cancelled states
    //
    // Caller: Client, TaskManager
    //
    long getTaskDuration();

    //TODO: finish comments
    // Set/Get additional information to the task
    //
    // Caller: Client
    //
    void setTaskUserData(Object data);
    Object getTaskUserData();

    // add/remove dependency
    //
    // Caller: Client
    //
    void addTaskDependency(Task task);
    void removeTaskDependency(Task task);
    boolean isBlocked();

    // TODO: add auto clear listeners
    // Add/Remove a listener to get status changes
    // after setting Finished or Cancelled states all listeners are cleared by TaskManager
    //
    // Caller: Client, TaskPool
    //
    void addTaskStatusListener(StatusListener listener);
    void removeTaskStatusListener(StatusListener listener);

    // TODO: add auto clear listeners
    // Add/Remove a Progress listener
    // after setting Finished or Cancelled states all listeners are cleared by TaskManager
    //
    // Caller: Client
    //
    void addTaskProgressListener(ProgressListener listener);
    void removeTaskProgressListener(ProgressListener listener);

    // Handy way to access private methods
    // should be called only in TaskManager, TaskPool
    //
    // Caller: TaskManager
    //
    TaskPrivate getPrivate();

    // Start the task
    // It's from private part but must be implemented on this level
    //
    // Caller: TaskManager
    //
    void startTask();

    enum Status {
        NotStarted, //not started
        Waiting, //in queue, must be set in the caller thread
        Blocked, //is waiting until all dependencies finish
        Started, //started
        Finished, //successfully loaded
        Cancelled //has cancelled but still is loading or cancelled while waiting in a queue
    }

    enum LoadPolicy {
        SkipIfAdded, // don't load if the state isn't equal to Waiting
        CancelAdded // cancel already added task, in this case you shouldn't do anything with cancelled task
    }

    // TODO: pass Error to get to know that the task was cancelled or an error happened
    interface Callback {
        //Here I put the cancelled as the argument to emphasise that it must be handled (also it can be got from status)
        void onCompleted(boolean cancelled);
    }

    interface StatusListener {
        void onTaskStatusChanged(Task task, Status oldStatus, Status newStatus);
    }

    interface ProgressListener {
        void onTaskProgressChanged(Task task, ProgressInfo progressInfo);
    }
}
