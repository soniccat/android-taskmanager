package com.example.alexeyglushkov.taskmanager.task;

import android.os.Handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alexeyglushkov on 11.06.17.
 */

public class TaskProviderWrapper implements TaskProvider {

    private TaskProvider provider;
    private Map<TaskPoolListener, TaskPoolListener> listenerMap = new HashMap<>();

    public TaskProviderWrapper(TaskProvider provider) {
        this.provider = provider;
    }

    @Override
    public void setHandler(Handler handler) {
        provider.setHandler(handler);
    }

    @Override
    public Handler getHandler() {
        return provider.getHandler();
    }

    @Override
    public void setTaskProviderId(String id) {
        provider.setTaskProviderId(id);
    }

    @Override
    public void addTask(Task task) {
        provider.addTask(task);
    }

    @Override
    public void removeTask(Task task) {
        provider.removeTask(task);
    }

    @Override
    public String getTaskProviderId() {
        return provider.getTaskProviderId();
    }

    @Override
    public Task getTask(String taskId) {
        return provider.getTask(taskId);
    }

    @Override
    public int getTaskCount() {
        return provider.getTaskCount();
    }

    @Override
    public List<Task> getTasks() {
        return provider.getTasks();
    }

    @Override
    public void setUserData(Object data) {
        provider.setUserData(data);
    }

    @Override
    public Object getUserData() {
        return provider.getUserData();
    }

    @Override
    public void addListener(final TaskPoolListener listener) {
        TaskPoolListener wrapperListener = new TaskPoolListener() {
            @Override
            public void onTaskAdded(TaskPool pool, Task task) {
                listener.onTaskAdded(TaskProviderWrapper.this, task);
            }

            @Override
            public void onTaskRemoved(TaskPool pool, Task task) {
                listener.onTaskRemoved(TaskProviderWrapper.this, task);
            }
        };

        listenerMap.put(listener, wrapperListener);
        provider.addListener(wrapperListener);
    }

    @Override
    public void setPriority(int priority) {
        provider.setPriority(priority);
    }

    @Override
    public void removeListener(TaskPoolListener listener) {
        TaskPoolListener wrappedListener = listenerMap.get(listener);
        if (wrappedListener != null) {
            provider.removeListener(wrappedListener);
            listenerMap.remove(listener);
        }
    }

    @Override
    public int getPriority() {
        return provider.getPriority();
    }

    @Override
    public Task getTopTask(List<Integer> typesToFilter) {
        return provider.getTopTask(typesToFilter);
    }

    @Override
    public Task takeTopTask(List<Integer> typesToFilter) {
        return provider.takeTopTask(typesToFilter);
    }

    @Override
    public void onTaskStatusChanged(Task task, Task.Status oldStatus, Task.Status newStatus) {
        provider.onTaskStatusChanged(task, oldStatus, newStatus);
    }
}
