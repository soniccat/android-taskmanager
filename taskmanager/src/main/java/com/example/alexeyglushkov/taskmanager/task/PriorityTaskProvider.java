package com.example.alexeyglushkov.taskmanager.task;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;

import com.example.alexeyglushkov.tools.HandlerTools;

import org.junit.Assert;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by alexeyglushkov on 30.12.14.
 */

// A task source for TaskManager

    // TODO: try to extend SimpleTaskPool to remove code duplications
public class PriorityTaskProvider implements TaskProvider, TaskPool {
    static final String TAG = "PriorityTaskProvider";

    private String providerId;
    private Handler handler;
    private Object userData;
    private List<TaskPoolListener> listeners;
    private int priority;

    // Task type -> priority queue
    private SparseArray<SortedList<Task>> taskQueues;

    public PriorityTaskProvider(Handler handler, String id) {
        providerId = id;
        taskQueues = new SparseArray<SortedList<Task>>();
        listeners = new ArrayList<TaskPoolListener>();
        setHandler(handler);
    }

    private SortedList<Task> createQueue() {
        return new SortedList<Task>(new Comparator<Task>() {
            @Override
            public int compare(Task lhs, Task rhs) {
                if (lhs.getTaskPriority() == rhs.getTaskPriority()) {
                    return 0;
                }

                if (lhs.getTaskPriority() > rhs.getTaskPriority()) {
                    return -1;
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
                SortedList<Task> queue = taskQueues.get(taskQueues.keyAt(i));
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
            SortedList<Task> queue = taskQueues.get(task.getTaskType());
            Task polledTask = getTopTask(queue, true);
            triggerOnTaskRemoved(polledTask);
        }

        return task;
    }

    private Task getTopTask(SortedList<Task> queue, boolean needPoll) {
        Task topTask = null;

        Iterator<Task> iterator = queue.iterator();
        while(iterator.hasNext()) {
            topTask = iterator.next();
            if (!topTask.isBlocked()) {
                if(needPoll) {
                    iterator.remove();
                }
                break;
            }
        }

        return topTask;
    }

    public void updatePriorities(final PriorityProvider provider) {
        HandlerTools.runOnHandlerThread(handler, new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < taskQueues.size(); i++) {
                    SortedList<Task> queue = taskQueues.valueAt(i);
                    for (Task t : taskQueues.get(taskQueues.keyAt(i))) {
                        t.setTaskPriority(provider.getPriority(t));
                    }

                    queue.updateSortedOrder();
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
            Log.e(TAG, "Can't put task " + task.getClass().toString() + " because it's already started " + task.getTaskStatus().toString());
            return;
        }

        // TaskProvider must set Waiting status on the current thread
        task.getPrivate().setTaskStatus(Task.Status.Waiting);

        HandlerTools.runOnHandlerThread(handler, new Runnable() {
            @Override
            public void run() {
                addTaskOnThread(task);
            }
        });
    }

    @Override
    public void removeTask(final Task task) {
        HandlerTools.runOnHandlerThread(handler, new Runnable() {
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

        SortedList<Task> queue = taskQueues.get(task.getTaskType());
        if (queue == null) {
            queue = createQueue();
            taskQueues.put(task.getTaskType(), queue);
        }

        queue.addInSortedOrder(task);
    }

    private boolean removeTaskFromQueue(Task task) {
        checkHandlerThread();

        SortedList<Task> queue = taskQueues.get(task.getTaskType());
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
            SortedList<Task> queue = taskQueues.get(taskQueues.keyAt(i));
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
            SortedList<Task> queue = taskQueues.get(taskQueues.keyAt(i));
            resultCount += queue.size();
        }

        return resultCount;
    }

    @Override
    public List<Task> getTasks() {
        checkHandlerThread();

        ArrayList<Task> tasks = new ArrayList<Task>();

        for (int i=0; i<taskQueues.size(); ++i) {
            SortedList<Task> queue = taskQueues.get(taskQueues.keyAt(i));
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
