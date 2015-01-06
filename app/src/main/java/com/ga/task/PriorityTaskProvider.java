package com.ga.task;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by alexeyglushkov on 30.12.14.
 */

// A task source for TaskManager

public class PriorityTaskProvider implements TaskProvider, TaskPool.TaskPoolListener {
    TaskPool taskPool;
    Handler handler;
    List<TaskProviderListener> listeners;
    Object userData;

    PriorityQueue<Task> taskQueue;

    public PriorityTaskProvider(Handler handler, TaskPool taskPool) {
        taskQueue = createPriorityQueue();

        listeners = new ArrayList<TaskProviderListener>();
        setHandler(handler);
        setTaskPool(taskPool);
    }

    private PriorityQueue<Task> createPriorityQueue() {
        return new PriorityQueue<Task>(11, new Comparator<Task>() {
            @Override
            public int compare(Task lhs, Task rhs) {
                return Tools.reverseIntCompare(lhs.getTaskPriority(), rhs.getTaskPriority());
            }
        });
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

        Log.d("prioirtyprovider", "in queue " + taskQueue.size() + " last priority " + task.getTaskPriority());

        return task;
    }

    public void updatePriorities(final PriorityProvider provider) {
        Tools.runOnHandlerThread(handler, new Runnable() {
            @Override
            public void run() {
                PriorityQueue<Task> queue = createPriorityQueue();

                for (Task t : taskQueue) {
                    t.setTaskPriority(provider.getPriority(t));
                    queue.add(t);
                }

                taskQueue = queue;
                Log.d("prioirtyprovider","queue replaced");
            }
        });
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
    public void setUserData(Object data) {
        this.userData = data;
    }

    @Override
    public Object getUserData() {
        return userData;
    }

    @Override
    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public Handler getHandler() {
        return handler;
    }

    private void addOnThread(final Task task) {
        Tools.runOnHandlerThread(handler, new Runnable() {
            @Override
            public void run() {
                taskQueue.add(task);

                for (TaskProviderListener listener : listeners) {
                    listener.onTaskAdded(task);
                }
            }
        });
    }

    private void removeOnThread(final Task task) {
        Tools.runOnHandlerThread(handler, new Runnable() {
            @Override
            public void run() {
                if (taskQueue.remove(task)) {
                    for (TaskProviderListener listener : listeners) {
                        listener.onTaskRemoved(task);
                    }
                }
            }
        });
    }

    private void checkHandlerThread() {
        assert Looper.myLooper() == handler.getLooper();
    }

    public interface PriorityProvider {
        int getPriority(Task task);
    }

    @Override
    public void addListener(TaskProviderListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(TaskProviderListener listener) {
        listeners.remove(listener);
    }
}
