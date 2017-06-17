package com.example.alexeyglushkov.taskmanager.task;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexeyglushkov on 11.06.17.
 */

public class RestorableTaskProvider extends TaskProviderWrapper {

    private boolean isRecording;
    private List<Task> activeTasks = new ArrayList<>();
    private List<Task> storedTasks = new ArrayList<>();

    public RestorableTaskProvider(TaskProvider provider) {
        super(provider);
    }

    @Override
    public void addTask(final Task task) {
        task.addTaskStatusListener(new Task.StatusListener() {
            @Override
            public void onTaskStatusChanged(Task task, Task.Status oldStatus, Task.Status newStatus) {
                if (Tasks.isTaskCompleted(task)) {
                    activeTasks.remove(task);

                    if (isRecording()) {
                        //task.setTaskCallback(originalCallback);
                        storedTasks.add(task);
                    }
                }
            }
        });

        super.addTask(task);
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
        return findTask(storedTasks, taskId);
    }

    public void restoreTaskCompletion(final String taskId, final Task.Callback callback, Handler handler, final Completion completion) {
        final Looper looper = Looper.myLooper();

        handler.post(new Runnable() {
            @Override
            public void run() {
                restoreTaskCompletionOnThread(taskId, callback, new Completion() {
                    @Override
                    public void completed(final Task task, final boolean isRestored) {
                        if (completion != null) {
                            Handler h = new Handler(looper);
                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    completion.completed(task, isRestored);
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
        boolean isRestored = false;
        Task restoredTask = null;
        Task activeTask = findTask(activeTasks, taskId);

        if (activeTask != null) {
            Task.Status status = activeTask.getTaskStatus();
            boolean canReplaceCompletion = status == Task.Status.Blocked || status == Task.Status.NotStarted || status == Task.Status.Waiting;

            if (canReplaceCompletion) {
                activeTask.setTaskCallback(callback);

            } else if (Tasks.isTaskCompleted(activeTask)) {
                callback.onCompleted(activeTask.getTaskStatus() == Task.Status.Cancelled);

            }else {
                Tasks.bindOnTaskCompletion(activeTask, new Tasks.TaskListener() {
                    @Override
                    public void setTaskInProgress(Task task) {
                    }

                    @Override
                    public void setTaskCompleted(Task task) {
                        callback.onCompleted(task.getTaskStatus() == Task.Status.Cancelled);
                    }
                });
            }

            isRestored = true;

        } else {
            restoredTask = findStoredTask(taskId);
            if (restoredTask != null) {
                isRestored = true;
            }
        }

        completion.completed(restoredTask, isRestored);
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
        storedTasks.clear();
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

        // restored is true if the task is restored,
        // in this case the task could be null or not null
        // it's null if the task is in progress or waiting and the passed completion will be called
        // it isn't null if the task is completed, the result of the task should be handled
        void completed(Task task, boolean isRestored);
    }
}
