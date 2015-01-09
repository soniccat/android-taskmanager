package com.ga.task;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;

import junit.framework.Assert;

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

    //TODO: think about weakref
    List<TaskProviderListener> listeners;
    Object userData;

    // Task type -> priority queue
    SparseArray<PriorityQueue<Task>> taskQueues;

    public PriorityTaskProvider(Handler handler, TaskPool taskPool) {
        taskQueues = new SparseArray<PriorityQueue<Task>>();

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
    public Task getTopTask(List<Integer> typesToFilter) {
        checkHandlerThread();

        Task topTask = null;
        int topPriority = -1;

        for (int i = 0; i < taskQueues.size(); i++) {
            if (!typesToFilter.contains(taskQueues.keyAt(i))) {
                PriorityQueue<Task> queue = taskQueues.get(taskQueues.keyAt(i));
                Task queueTask = queue.peek();
                if (queueTask != null && queueTask.getTaskPriority() > topPriority) {
                    topTask = queueTask;
                    topPriority = topTask.getTaskPriority();
                }
            }
        }

        return topTask;
    }

    @Override
    public Task takeTopTask(List<Integer> typesToFilter) {
        checkHandlerThread();

        Task task = getTopTask(typesToFilter);
        if (task != null) {
            taskQueues.get(task.getTaskType()).poll();
            getTaskPool().removeTask(task);
        }

        return task;
    }

    public void updatePriorities(final PriorityProvider provider) {
        Tools.runOnHandlerThread(handler, new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < taskQueues.size(); i++) {

                    PriorityQueue<Task> queue = createPriorityQueue();
                    for (Task t : taskQueues.get(taskQueues.keyAt(i))) {
                        t.setTaskPriority(provider.getPriority(t));
                        queue.add(t);
                    }

                    taskQueues.put(taskQueues.keyAt(i), queue);
                }
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
    public void onTaskAdded(TaskPool pool, Task task) {
        addOnThread(task);
    }

    @Override
    public void onTaskRemoved(TaskPool pool, Task task) {
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
                addTaskToQueue(task);

                for (TaskProviderListener listener : listeners) {
                    listener.onTaskAdded(PriorityTaskProvider.this,task);
                }
            }
        });
    }

    private void removeOnThread(final Task task) {
        Tools.runOnHandlerThread(handler, new Runnable() {
            @Override
            public void run() {
                if (removeTaskFromQueue(task)) {
                    for (TaskProviderListener listener : listeners) {
                        listener.onTaskRemoved(PriorityTaskProvider.this,task);
                    }
                }
            }
        });
    }

    private void addTaskToQueue(Task task) {
        PriorityQueue<Task> queue = taskQueues.get(task.getTaskType());
        if (queue == null) {
            queue = createPriorityQueue();
            taskQueues.put(task.getTaskType(), queue);
        }

        queue.add(task);
    }

    private boolean removeTaskFromQueue(Task task) {
        PriorityQueue<Task> queue = taskQueues.get(task.getTaskType());
        if (queue != null) {
            return queue.remove(task);
        }

        return false;
    }

    private void checkHandlerThread() {
        Assert.assertEquals(Looper.myLooper(), handler.getLooper());
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
