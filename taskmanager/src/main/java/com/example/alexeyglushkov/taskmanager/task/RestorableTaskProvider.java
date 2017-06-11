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
                    if (!isRecording()) {
                        //task.setTaskCallback(originalCallback);
                        activeTasks.remove(task);
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
                    public void completed(final boolean isRestored) {
                        if (completion != null) {
                            Handler h = new Handler(looper);
                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    completion.completed(isRestored);
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private void restoreTaskCompletionOnThread(String taskId, final Task.Callback callback, final Completion completion) {
        final Looper looper = Looper.myLooper();

        boolean isRestored = false;
        Task activeTask = findTask(activeTasks, taskId);

        if (activeTask != null) {
            Task.Status status = activeTask.getTaskStatus();
            boolean canReplaceCompletion = status == Task.Status.Blocked || status == Task.Status.NotStarted || status == Task.Status.Waiting;

            if (canReplaceCompletion) {
                activeTask.setTaskCallback(callback);

            } else {
                activeTask.addTaskStatusListener(new Task.StatusListener() {
                    @Override
                    public void onTaskStatusChanged(Task task, Task.Status oldStatus, final Task.Status newStatus) {
                        if (Tasks.isTaskCompleted(task)) {
                            Handler h = new Handler(looper);
                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onCompleted(newStatus == Task.Status.Cancelled);
                                }
                            });
                        }
                    }
                });
            }

            isRestored = true;
        }

        completion.completed(isRestored);
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

    public void setRecording(boolean recording) {
        isRecording = recording;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public interface Completion {
        void completed(boolean isRestored);
    }
}
