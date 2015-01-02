package com.ga.task;

import android.os.Handler;
import android.os.Looper;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Created by alexeyglushkov on 30.12.14.
 */

// A task source for TaskManager

public class PriorityTaskProvider implements TaskProvider, TaskPool.TaskPoolListener {
    TaskPool taskPool;
    Handler handler;

    PriorityQueue<Task> taskQueue;

    public PriorityTaskProvider(Handler handler, TaskPool taskPool) {
        taskQueue = new PriorityQueue<Task>(11, new Comparator<Task>() {
            @Override
            public int compare(Task lhs, Task rhs) {
                return Tools.compare(lhs.getTaskPriority(), rhs.getTaskPriority());
            }
        });

        setHandler(handler);
        setTaskPool(taskPool);
    }

    @Override
    public Task getTopTask() {
        checkHandlerThread();

        return taskQueue.peek();
    }

    @Override
    public Task takeTopTask() {
        checkHandlerThread();

        Task task = taskQueue.poll();
        if (task != null) {
            getTaskPool().removeTask(task);
        }

        return task;
    }

    @Override
    public void setTaskPool(TaskPool pool) {
        if (taskPool != null) {
            taskPool.removeListener(this);
        }

        taskPool = pool;
        taskPool.addListener(this);
    }

    @Override
    public TaskPool getTaskPool() {
        return taskPool;
    }

    @Override
    public void onTaskAdded(Task task) {
        addOnThread(task);
    }

    @Override
    public void onTaskRemoved(Task task) {
        removeOnThread(task);
    }

    @Override
    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    private void addOnThread(final Task task) {
        Tools.runOnHandlerThread(handler, new Runnable() {
            @Override
            public void run() {
                taskQueue.add(task);
            }
        });
    }

    private void removeOnThread(final Task task) {
        Tools.runOnHandlerThread(handler, new Runnable() {
            @Override
            public void run() {
                taskQueue.remove(task);
            }
        });
    }

    private void checkHandlerThread() {
        assert Looper.myLooper() == handler.getLooper();
    }
}
