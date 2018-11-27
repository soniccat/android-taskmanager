package com.example.alexeyglushkov.taskmanager.task;

import android.os.Handler;

import java.util.List;

/**
 * Created by alexeyglushkov on 13.08.16.
 */
public class StackTaskProvider extends SimpleTaskPool implements TaskProvider, Task.StatusListener {
    private String providerId;
    private int priority;

    private boolean areTasksDependent; //if enabled the top task blocks the next task until the former finishes
    private boolean isBlocked;

    public StackTaskProvider(boolean areTasksDependent, Handler handler, String id) {
        super(handler);

        this.providerId = id;
        this.areTasksDependent = areTasksDependent;
    }

    @Override
    public void setTaskProviderId(String id) {
        providerId = id;
    }

    @Override
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
    protected void triggerOnTaskAdded(Task task) {
        if (!isBlocked) {
            super.triggerOnTaskAdded(task);
        }
    }

    @Override
    public Task getTopTask(List<Integer> typesToFilter) {
        Task result = null;
        if (canTakeTask()) {
            int index = getTopTaskIndex(typesToFilter);
            if (index != -1) {
                result = getTasks().get(index);
            }
        }

        return result;
    }

    @Override
    public Task takeTopTask(List<Integer> typesToFilter) {
        Task result = null;
        if (canTakeTask()) {
            int index = getTopTaskIndex(typesToFilter);
            result = getTasks().get(index);
            getTasks().remove(index);
            onTaskTaken(result);
        }

        return result;
    }

    private void onTaskTaken(Task task) {
        if (areTasksDependent) {
            isBlocked = true;

            task.addTaskStatusListener(new Task.StatusListener() {
                @Override
                public void onTaskStatusChanged(Task task, Task.Status oldStatus, Task.Status newStatus) {
                    if (Tasks.isTaskCompleted(task)) {
                        isBlocked = false;

                        if (getTaskCount() > 0) {
                            // TODO: trigger for the next tasks until not blocked
                            triggerOnTaskAdded(getTasks().get(0));
                        }
                    }
                }
            });
        }

        triggerOnTaskRemoved(task);
    }

    private void triggerOnTaskRemoved(Task task) {
        checkHandlerThread();

        for (TaskPoolListener listener : listeners) {
            listener.onTaskRemoved(StackTaskProvider.this, task);
        }
    }

    private boolean canTakeTask() {
        return !areTasksDependent || !isBlocked;
    }

    private int getTopTaskIndex(List<Integer> typesToFilter) {
        int result = -1;
        for (int i=0; i<getTasks().size(); ++i) {
            Task task = getTasks().get(i);

            boolean passFilter = typesToFilter == null || !typesToFilter.contains(task.getTaskType());
            if (!task.isBlocked() && passFilter) {
                result = i;
                break;
            }
        }

        return result;
    }
}
