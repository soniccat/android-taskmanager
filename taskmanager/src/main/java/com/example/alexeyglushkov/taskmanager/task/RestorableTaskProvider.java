package com.example.alexeyglushkov.taskmanager.task;

import android.os.Handler;
import android.os.Looper;

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

    private boolean isRecording;
    private List<Task> activeTasks = new ArrayList<>();
    private List<Task> storedTasks = new ArrayList<>();

    public RestorableTaskProvider(TaskProvider provider) {
        super(provider);
    }

    @Override
    public void addTask(final Task task) {
        super.addTask(task);
        if (!Tasks.isTaskReadyToStart(task)) {
            // that was rejected on super level too
            return;
        }

        task.addTaskStatusListener(this);
    }

    @Override
    public void onTaskStatusChanged(Task task, Task.Status oldStatus, Task.Status newStatus) {
        if (Tasks.isTaskCompleted(task)) {
            activeTasks.remove(task);

            if (isRecording()) {
                getStoredTasks().add(task);
            }
        }
    }

    @Override
    public Task takeTopTask(List<Integer> typesToFilter) {
        Task task = super.takeTopTask(typesToFilter);
        activeTasks.add(task);

        return task;
    }

    public List<Task> getStoredTasks() {
        return storedTasks;
    }

    public Task findStoredTask(String taskId) {
        return findTask(getStoredTasks(), taskId);
    }

    public void restoreTaskCompletion(final String taskId, final Task.Callback callback, Handler handler, final Completion completion) {
        final Looper looper = Looper.myLooper();

        handler.post(new Runnable() {
            @Override
            public void run() {
                restoreTaskCompletionOnThread(taskId, callback, new Completion() {
                    @Override
                    public void completed(final Task task, final RestoreState restoreState) {
                        if (completion != null) {
                            Handler h = new Handler(looper);
                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    completion.completed(task, restoreState);
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    // try to update the completion of the task if the task is in progress or waiting
    // otherwise return the completed task in completion
    private void restoreTaskCompletionOnThread(String taskId, final Task.Callback callback, final Completion completion) {
        RestoreState restoreState = RestoreState.NotRestored;
        Task task = findTask(activeTasks, taskId);

        if (task != null) {
            Task.Status status = task.getTaskStatus();
            boolean canReplaceCompletion = status == Task.Status.Blocked || status == Task.Status.NotStarted || status == Task.Status.Waiting;
            restoreState = RestoreState.ReplacedCompletion;

            if (canReplaceCompletion) {
                task.setTaskCallback(callback);

            } else if (Tasks.isTaskCompleted(task)) {
                callback.onCompleted(task.getTaskStatus() == Task.Status.Cancelled);

            }else {
                Tasks.bindOnTaskCompletion(task, new Tasks.TaskListener() {
                    @Override
                    public void setTaskInProgress(Task task) {
                    }

                    @Override
                    public void setTaskCompleted(Task task) {
                        callback.onCompleted(task.getTaskStatus() == Task.Status.Cancelled);
                    }
                });
            }

        } else {
            task = findStoredTask(taskId);
            if (task != null) {
                restoreState = RestoreState.Restored;
            }
        }

        completion.completed(task, restoreState);
    }

    private Task findTask(List<Task> tasks, String taskId) {
        Task result = null;
        for (Task task : tasks) {
            if (task.getTaskId().equals(taskId)) {
                result = task;
                break;
            }
        }

        return result;
    }

    public void clearStoredTasks() {
        getStoredTasks().clear();
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
}
