package com.example.alexeyglushkov.taskmanager.task;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.Pair;
import android.util.Log;

import com.example.alexeyglushkov.tools.HandlerTools;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexeyglushkov on 11.06.17.
 */

public class RestorableTaskProvider extends TaskProviderWrapper {
    public enum RestoreState {
        NotRestored, // task wasn't found
        Restored, // completed task was found and should be handled
        ReplacedCompletion // task is in progress, completion will be called when the task finishes
    }

    static final String TAG = "RestorableTaskProvider";
    static private final boolean isDebug = false;

    private boolean isRecording;
    protected List<Task> activeTasks = new ArrayList<>();
    protected List<Task> completedTasks = new ArrayList<>();

    public RestorableTaskProvider(TaskProvider provider) {
        super(provider);
    }

    @Override
    public void addTask(final Task task) {
        if (!Tasks.isTaskReadyToStart(task)) {
            Log.e(TAG, "Can't put task " + task.getClass().toString() + " because it's already started " + task.getTaskStatus().toString());
            return;
        }

        super.addTask(task);
        if (task.getTaskStatus() == Task.Status.NotStarted) {
            return;
        }

        task.addTaskStatusListener(this);

        if (isDebug) {
            Log.d(TAG, "activeTasks addTaskStatusListener " + task + " status " + task.getTaskStatus());
        }
    }

    @Override
    public void onTaskStatusChanged(Task task, Task.Status oldStatus, Task.Status newStatus) {
        if (isDebug) {
            Log.d(TAG, "activeTasks onTaskStatusChanged " + task + " from " + oldStatus + " to " + newStatus);
        }

        if (Tasks.isTaskCompleted(task)) {
            checkHandlerThread();
            activeTasks.remove(task);

            if (isDebug) {
                Log.d(TAG, "activeTasks removed " + task);
            }

            if (isRecording()) {
                getCompletedTasks().add(task);
            }
        }
    }

    @Override
    public Task takeTopTask(List<Integer> typesToFilter) {
        checkHandlerThread();

        Task task = super.takeTopTask(typesToFilter);
        if (task != null) {
            activeTasks.add(task);

            if (isDebug) {
                Log.d(TAG, "activeTasks added " + task);
            }
        }

        return task;
    }

    protected List<Task> getCompletedTasks() {
        return completedTasks;
    }

    private Task findCompletedTask(String taskId) {
        return findTask(getCompletedTasks(), taskId);
    }

    public void restoreTaskCompletion(final String taskId, final Task.Callback callback) {
        final Looper looper = Looper.myLooper();
        restoreTaskCompletion(taskId, getDefaultCompletion(callback, looper));
    }

    public void restoreTaskCompletion(final String taskId, final Completion completion) {
        HandlerTools.runOnHandlerThread(getHandler(), new Runnable() {
            @Override
            public void run() {
                Pair<Task, RestoreState> restoredData = restoreTaskCompletionOnThread(taskId);
                completion.completed(restoredData.first, restoredData.second);
            }
        });
    }

    public Completion getDefaultCompletion(final Task.Callback taskCallback, final Looper callbackLooper) {
        return new Completion() {
            @Override
            public void completed(final Task restoredTask, RestoreState restoredState) {
                if (restoredState == RestoreState.ReplacedCompletion) {
                    // that callback is handled in task manager instead of startCallback
                    restoredTask.setTaskCallback(taskCallback);

                } else if (restoredState == RestoreState.Restored) {
                    Handler h = new Handler(callbackLooper);
                    HandlerTools.runOnHandlerThread(h, new Runnable() {
                        @Override
                        public void run() {
                            taskCallback.onCompleted(restoredTask.getTaskStatus() == Task.Status.Cancelled);
                        }
                    });
                }
            }
        };
    }

    private Pair<Task, RestoreState> restoreTaskCompletionOnThread(String taskId) {
        checkHandlerThread();

        RestoreState restoreState = RestoreState.NotRestored;
        Task task = findTask(activeTasks, taskId);

        if (task != null) {
            if (Tasks.isTaskCompleted(task)) {
                // if a task is completed it will be in completedTasks
                Assert.fail("Can't stop here");
            }else {
                restoreState = RestoreState.ReplacedCompletion;
            }

        } else {
            task = findCompletedTask(taskId);
            if (task != null) {
                restoreState = RestoreState.Restored;
            }
        }

        return new Pair<>(task, restoreState);
    }

    private Task findTask(List<Task> tasks, String taskId) {
        checkHandlerThread();

        Task result = null;
        for (Task task : tasks) {
            if (task.getTaskId().equals(taskId)) {
                result = task;
                break;
            }
        }

        return result;
    }

    private void clearStoredTasks() {
        getCompletedTasks().clear();
    }

    public void setRecording(boolean recording) {
        isRecording = recording;

        if (!isRecording) {
            clearStoredTasks();
        }
    }

    public boolean isRecording() {
        return isRecording;
    }

    public interface Completion {
        void completed(Task task, RestoreState state);
    }

    protected void checkHandlerThread() {
        Assert.assertEquals(Looper.myLooper(), getHandler().getLooper());
    }
}
