package com.ga.task;

import android.os.Looper;

import com.example.rssreader.Tools;

/**
 * Created by alexeyglushkov on 28.12.14.
 */
public class Tasks {

    // To automatically sync task state with your object state (isLoading for example)
    public static void bindOnTaskCompletion(final Task task, final TaskListener listener) {
        assert task.getTaskStatus() == Task.Status.NotStarted;

        if (task.getTaskId() != null) {
            task.setTaskId(task.getTaskId() + listener.hashCode());
        }

        task.addTaskStatusListener(new Task.StatusListener() {
            @Override
            public void onTaskStatusChanged(final Task task, final Task.Status oldStatus, final Task.Status newStatus) {
                //Waiting status is set in the main thread
                if (newStatus == Task.Status.Waiting) {
                    assert (Looper.myLooper() == Looper.getMainLooper());
                    listener.setTaskInProgress(task);

                } else if (newStatus == Task.Status.Finished || newStatus == Task.Status.Cancelled)
                Tools.postOnMainLoop(new Runnable() {
                    @Override
                    public void run() {
                        listener.setTaskCompleted(task);
                    }
                });
            }
        });
    }

    public static boolean isTaskReadyToStart(Task task) {
        Task.Status st = task.getTaskStatus();
        return st != Task.Status.Started && st != Task.Status.Finished && st != Task.Status.Cancelled;
    }

    public static boolean isTaskCompleted(Task task) {
        Task.Status st = task.getTaskStatus();
        return st == Task.Status.Finished || st == Task.Status.Cancelled;
    }

    // TODO: think about a better name
    // An implementer should store task and filter setTaskCompleted call with old task
    // This can happen due to task cancellation behavior. When a task was cancelled when completion block
    // was already added to the main thread
    public interface TaskListener {
        void setTaskInProgress(Task task);
        void setTaskCompleted(Task task);
    }
}
