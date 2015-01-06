package com.ga.task;

import java.util.Date;

/**
 * Created by alexeyglushkov on 20.09.14.
 */

// A task represents an action that takes time to finish. It can be downloading data, processing information and other.
// The task can be started only once. If you want to repeat it you should create new one.
// The task mustn't work with model data stored outside the task, model data should be changed on main thread after task completion.

public interface Task extends TaskContainer {

    // Start the task
    //
    // Caller: TaskManager
    // Thread: TaskManager's thread
    //
    // Callback caller: TaskManager
    // Callback Thread: Main thread
    // If you would like to have an additional action on the task thread you should extend your class
    // and do all what you need in the start method
    void startTask();
    void setTaskCallback(Callback callback);
    Callback getTaskCallback();

    //TODO: add custom thread callback support through set/get

    // Marks the task that it should be cancelled
    // it will have Cancelled state if the task was started and successfully cancelled
    // if it wasn't possible to cancel the task the state will be set to Finished
    // otherwise NotStarted state will be set
    //
    // Caller: TaskManager
    // Thread: TaskManager's thread
    void cancelTask(Object info);
    boolean getNeedCancelTask();

    // Return info passed in the cancel method
    Object getCancellationInfo();

    // Set/Get current state of the task
    //
    // Caller: TaskManager
    // Thread: TaskManager's thread
    void setTaskStatus(Status status);
    Status getTaskStatus();

    // Set/Get current type of the task
    //
    // Caller: Client, TaskManager
    // Thread: Main thread, TaskManager's thread
    void setTaskType(int type);
    int getTaskType();

    // Set/Get the task's id. Tasks with the same id means that they download the same data for the same caller.
    // It's useful when you want to bind the task to a specific model object and provide an additional load policy
    // The right load policy can prevent needless loadings
    // nil means that the task doesn't have the id
    //
    // Caller: Client
    // Thread: Main thread
    void setTaskId(String id);
    String getTaskId();

    // For tasks with the same id TaskManager uses a load policy to handle them
    //
    // Caller: Client, TaskManager, TaskPool
    // Thread: caller thread
    void setLoadPolicy(LoadPolicy loadPolicy);
    LoadPolicy getLoadPolicy();

    // Return the error of the task happened during the execution
    // Basically it should be called in the start callback
    //
    // Caller: Client
    // Thread: Main thread
    Error getTaskError();

    // Set/Get the priority of the task
    //
    // Caller: Client
    // Thread: Main thread
    void setTaskPriority(int value);
    int getTaskPriority();

    // Set/Get the priority of the task
    //
    // Caller: Client, Task
    // Thread: Main thread
    float getTaskProgress();
    void setTaskProgressMinChange(float value);

    void setTaskStartDate(Date date);
    long getTaskDuration();

    //TODO: finish comments

    void setTaskUserData(Object data);
    Object getTaskUserData();

    // Caller:
    // Thread:
    void addTaskDependency(Task task);
    void removeTaskDependency(Task task);

    // Caller:
    // Thread:
    void addTaskStatusListener(StatusListener listener);
    void removeTaskStatusListener(StatusListener listener);

    // Caller:
    // Thread:
    void addTaskProgressListener(ProgressListener listener);
    void removeTaskProgressListener(ProgressListener listener);

    public enum Status {
        NotStarted, //not started
        Waiting, //in queue, must be set in the caller thread
        Blocked, //is waiting until all dependencies finish
        Started, //started
        Finished, //successfully loaded
        Cancelled //has cancelled but still is loading
    }

    public enum LoadPolicy {
        SkipIfAdded, // don't load if Task's state isn't equal to Waiting
        CancelAdded // cancel already added task, in this case you shouldn't do anything with cancelled task
    }

    public interface Callback {
        void finished();
    }

    public interface StatusListener {
        void onTaskStatusChanged(Task task, Status oldStatus, Status newStatus);
    }

    public interface ProgressListener {
        void onTaskProgressChanged(Task task, float oldValue, float newValue);
    }
}
