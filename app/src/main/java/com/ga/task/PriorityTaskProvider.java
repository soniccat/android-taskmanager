package com.ga.task;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by alexeyglushkov on 30.12.14.
 */

// A task source for TaskManager

public class PriorityTaskProvider implements TaskProvider, TaskPool, Task.StatusListener {
    static final String TAG = "PriorityTaskProvider";

    private Handler handler;
    private Object userData;
    private List<TaskPoolListener> listeners;

    // Task type -> priority queue
    private SparseArray<PriorityQueue<Task>> taskQueues;

    public PriorityTaskProvider(Handler handler) {
        taskQueues = new SparseArray<PriorityQueue<Task>>();
        listeners = new ArrayList<TaskPoolListener>();
        setHandler(handler);
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
            if (typesToFilter == null || !typesToFilter.contains(taskQueues.keyAt(i))) {
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
            triggerOnTaskRemoved(task);
        }

        return task;
    }

    // TODO: rewrite using getFilteredTasks
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
                Log.d("prioirtyprovider", "queue replaced");
            }
        });
    }

    @Override
    public void addTask(final Task task) {
        if (!Tasks.isTaskReadyToStart(task)) {
            Log.d(TAG, "Can't put task " + task.getClass().toString() + " because it's already started " + task.getTaskStatus().toString());
            return;
        }

        task.getPrivate().setTaskStatus(Task.Status.Waiting);

        Tools.runOnHandlerThread(handler, new Runnable() {
            @Override
            public void run() {
                addTaskOnThread(task);
            }
        });
    }

    @Override
    public void removeTask(final Task task) {
        Tools.runOnHandlerThread(handler, new Runnable() {
            @Override
            public void run() {
                removeTaskOnThread(task);
            }
        });
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
        checkHandlerThread();

        if (removeTaskFromQueue(task)) {
            triggerOnTaskRemoved(task);
        }
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
        for (int i=0; i<taskQueues.size() && resultTask == null; ++i) {
            PriorityQueue<Task> queue = taskQueues.get(taskQueues.keyAt(i));
            Iterator<Task> iterator = queue.iterator();

            while (iterator.hasNext()) {
                Task task = iterator.next();
                if (task.getTaskId() != null && task.getTaskId().equals(taskId)) {
                    resultTask = task;
                    break;
                }
            }
        }

        return resultTask;
    }

    @Override
    public int getTaskCount() {
        checkHandlerThread();

        int resultCount = 0;
        for (int i=0; i<taskQueues.size(); ++i) {
            PriorityQueue<Task> queue = taskQueues.get(taskQueues.keyAt(i));
            resultCount += queue.size();
        }

        return resultCount;
    }

    @Override
    public List<Task> getTasks() {
        checkHandlerThread();

        ArrayList<Task> tasks = new ArrayList<Task>();

        for (int i=0; i<taskQueues.size(); ++i) {
            PriorityQueue<Task> queue = taskQueues.get(taskQueues.keyAt(i));
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
