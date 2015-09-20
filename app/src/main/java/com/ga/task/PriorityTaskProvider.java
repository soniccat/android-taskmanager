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

import static junit.framework.Assert.assertTrue;

/**
 * Created by alexeyglushkov on 30.12.14.
 */

// A task source for TaskManager

public class PriorityTaskProvider implements TaskProvider, TaskPool, Task.StatusListener {
    static final String TAG = "PriorityTaskProvider";

    private String providerId;
    private Handler handler;
    private Object userData;
    private List<TaskPoolListener> listeners;
    private int priority;

    // Task type -> priority queue
    private SparseArray<PriorityQueue<Task>> taskQueues;

    public PriorityTaskProvider(Handler handler, String id) {
        providerId = id;
        taskQueues = new SparseArray<PriorityQueue<Task>>();
        listeners = new ArrayList<TaskPoolListener>();
        setHandler(handler);
    }

    private PriorityQueue<Task> createQueueSet() {
        return new PriorityQueue<Task>(11, new Comparator<Task>() {
            @Override
            public int compare(Task lhs, Task rhs) {
                if (lhs == rhs) {
                    return 0;
                }

                if (lhs.getTaskPriority() > rhs.getTaskPriority()) {
                    return -1;
                }

                if (lhs.getTaskPriority() == rhs.getTaskPriority()) {
                    return 0;
                }

                return 1;
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
                Task queueTask = getTopTask(queue, false);

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
            PriorityQueue<Task> queue = taskQueues.get(task.getTaskType());
            Task polledTask = getTopTask(queue, true);
            triggerOnTaskRemoved(polledTask);
        }

        return task;
    }

    // TODO: find a better solution, TreeSet isn't great because it requires unique objects and
    // it's hard to achieve it with tasks using comparator, we just don't have enough information.
    // Also deletion in TreeSet happens via comparator and it must return 0 only for equal objects.
    private Task getTopTask(PriorityQueue<Task> queue, boolean needPoll) {
        Task topTask = null;

        ArrayList<Task> polledTasks = new ArrayList<Task>();
        while (!queue.isEmpty()) {
            if (!queue.peek().isBlocked()) {
                topTask = needPoll ? queue.poll() : queue.peek();
                break;
            } else {
                polledTasks.add(queue.poll());
            }
        }

        for (Task task : polledTasks) {
            queue.add(task);
        }

        return topTask;
    }

    public void updatePriorities(final PriorityProvider provider) {
        Tools.runOnHandlerThread(handler, new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < taskQueues.size(); i++) {
                    PriorityQueue<Task> queue = createQueueSet();
                    for (Task t : taskQueues.get(taskQueues.keyAt(i))) {
                        t.setTaskPriority(provider.getPriority(t));
                        queue.add(t);
                    }

                    taskQueues.put(taskQueues.keyAt(i), queue);
                }
            }
        });
    }

    public void setTaskProviderId(String id) {
        providerId = id;
    }

    public String getTaskProviderId() {
        return providerId;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void addTask(final Task task) {
        if (!Tasks.isTaskReadyToStart(task)) {
            Log.d(TAG, "Can't put task " + task.getClass().toString() + " because it's already started " + task.getTaskStatus().toString());
            return;
        }

        // TaskProvider must set Waiting status on the current thread
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
        if (this.handler != null) {
            checkHandlerThread();
        }

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
            queue = createQueueSet();
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
