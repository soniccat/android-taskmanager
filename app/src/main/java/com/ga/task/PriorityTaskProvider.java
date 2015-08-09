package com.ga.task;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Created by alexeyglushkov on 30.12.14.
 */

// A task source for TaskManager

public class PriorityTaskProvider implements TaskProvider, TaskPool, Task.StatusListener {
    static final String TAG = "PriorityTaskProvider";

    private Handler handler;
    private Object userData;

    //TODO: think about weakref
    private List<TaskPoolListener> listeners;

    // Task type -> priority queue
    // map is used because SparseArray can't be tested via junit
    private Map<Integer,PriorityQueue<Task>> taskQueues;

    public PriorityTaskProvider(Handler handler) {
        taskQueues = new HashMap<Integer, PriorityQueue<Task>>();

        listeners = new ArrayList<TaskPoolListener>();
        setHandler(handler);
    }

    protected PriorityQueue<Task> createPriorityQueue() {
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

        for (Map.Entry<Integer, PriorityQueue<Task>> entry : taskQueues.entrySet()) {
            if (!typesToFilter.contains(entry.getKey())) {
                PriorityQueue<Task> queue = entry.getValue();
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
            triggerOnTaskRemoved(task);
        }

        return task;
    }

    public void updatePriorities(final PriorityProvider provider) {
        Tools.runOnHandlerThread(handler, new Runnable() {
            @Override
            public void run() {
                for (Map.Entry<Integer, PriorityQueue<Task>> entry : taskQueues.entrySet()) {
                    PriorityQueue<Task> queue = createPriorityQueue();
                    for (Task t : entry.getValue()) {
                        t.setTaskPriority(provider.getPriority(t));
                        queue.add(t);
                    }

                    taskQueues.put(entry.getKey(), queue);
                }
                Log.d("prioirtyprovider", "queue replaced");
            }
        });
    }

    @Override
    public void addTask(final Task task) {
        if (!Tasks.isTaskReadyToStart(task)) {
            Log.d(TAG, "Can't put task " + task.getClass().toString() + " because it has been added " + task.getTaskStatus().toString());
            return;
        }

        // TODO: think about the same codebase here in pool and in TaskManager, it would be better
        // to have it only in the TaskManager
        // TaskManager must set Waiting status on the current thread
        task.getPrivate().setTaskStatus(Task.Status.Waiting);

        Tools.runOnHandlerThread(handler, new Runnable() {
            @Override
            public void run() {
                addTaskOnThread(task);
            }
        });
    }

    @Override
    public void removeTask(Task task) {
        removeTaskOnThread(task);
    }

    public void onTaskStatusChanged(Task task, Task.Status oldStatus, Task.Status newStatus) {
        if (Tasks.isTaskCompleted(task)) {
            removeTask(task);
        }
    }

    @Override
    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public Handler getHandler() {
        return handler;
    }

    private void addTaskOnThread(final Task task) {
        task.addTaskStatusListener(PriorityTaskProvider.this);
        addTaskToQueue(task);

        for (TaskPoolListener listener : listeners) {
            listener.onTaskAdded(PriorityTaskProvider.this, task);
        }
    }

    private void removeTaskOnThread(final Task task) {
        Tools.runOnHandlerThread(handler, new Runnable() {
            @Override
            public void run() {
                if (removeTaskFromQueue(task)) {
                    triggerOnTaskRemoved(task);
                }
            }
        });
    }

    private void triggerOnTaskRemoved(Task task) {
        checkHandlerThread();

        for (TaskPoolListener listener : listeners) {
            listener.onTaskRemoved(PriorityTaskProvider.this, task);
        }
    }

    private void addTaskToQueue(Task task) {
        checkHandlerThread();

        PriorityQueue<Task> queue = taskQueues.get(task.getTaskType());
        if (queue == null) {
            queue = createPriorityQueue();
            taskQueues.put(task.getTaskType(), queue);
        }

        queue.add(task);
    }

    private boolean removeTaskFromQueue(Task task) {
        checkHandlerThread();

        PriorityQueue<Task> queue = taskQueues.get(task.getTaskType());
        if (queue != null) {
            return queue.remove(task);
        }

        return false;
    }

    public interface PriorityProvider {
        int getPriority(Task task);
    }

    @Override
    public Task getTask(String taskId) {
        checkHandlerThread();

        Task resultTask = null;
        for (Map.Entry<Integer, PriorityQueue<Task>> entry : taskQueues.entrySet()) {
            PriorityQueue<Task> queue = entry.getValue();
            Iterator<Task> iterator = queue.iterator();

            while (iterator.hasNext()) {
                Task task = iterator.next();
                if (task.getTaskId() != null && task.getTaskId().equals(taskId)) {
                    resultTask = task;
                    break;
                }
            }

            if (resultTask != null) {
                break;
            }
        }

        return resultTask;
    }

    @Override
    public int getTaskCount() {
        checkHandlerThread();

        int resultCount = 0;
        for (Map.Entry<Integer, PriorityQueue<Task>> entry : taskQueues.entrySet()) {
            PriorityQueue<Task> queue = entry.getValue();
            resultCount += queue.size();
        }

        return resultCount;
    }

    @Override
    public List<Task> getTasks() {
        checkHandlerThread();

        ArrayList<Task> tasks = new ArrayList<Task>();

        for (Map.Entry<Integer, PriorityQueue<Task>> entry : taskQueues.entrySet()) {
            PriorityQueue<Task> queue = entry.getValue();
            tasks.addAll(queue);
        }

        return tasks;
    }

    @Override
    public void setUserData(Object data) {
        userData = data;
    }

    @Override
    public Object getUserData() {
        return userData;
    }

    @Override
    public void addListener(TaskPoolListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(TaskPoolListener listener) {
        listeners.remove(listener);
    }

    private void checkHandlerThread() {
        Assert.assertEquals(Looper.myLooper(), handler.getLooper());
    }
}
