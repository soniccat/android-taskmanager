package com.example.alexeyglushkov.taskmanager.task;

import android.os.Looper;
import android.util.Log;

import junit.framework.Assert;

/**
 * Created by alexeyglushkov on 28.12.14.
 */
public class Tasks {

    // To automatically sync task state with your object state (isLoading for example)
    public static void bindOnTaskCompletion(final Task task, final TaskListener listener) {
        Assert.assertEquals(task.getTaskStatus(), Task.Status.NotStarted);

        if (task.getTaskId() != null) {
            task.setTaskId(task.getTaskId() + listener.hashCode());
        }

        task.addTaskStatusListener(new Task.StatusListener() {
            @Override
            public void onTaskStatusChanged(final Task task, final Task.Status oldStatus, final Task.Status newStatus) {
                final Task.StatusListener thisListener = this;

                Log.d("Image--", "start " + task + " " + task.getTaskStatus());

                //Waiting status is set in the main thread
                if (newStatus == Task.Status.Waiting) {
                    Assert.assertEquals(Looper.myLooper(), Looper.getMainLooper());
                    listener.setTaskInProgress(task);

                } else if (newStatus == Task.Status.Finished || newStatus == Task.Status.Cancelled)
                    Tools.runOnMainThread(new Runnable() {
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