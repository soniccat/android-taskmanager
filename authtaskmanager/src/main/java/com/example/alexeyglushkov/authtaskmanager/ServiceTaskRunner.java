package com.example.alexeyglushkov.authtaskmanager;

import android.os.Handler;

import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner;
import com.example.alexeyglushkov.taskmanager.task.PriorityTaskProvider;
import com.example.alexeyglushkov.taskmanager.task.TaskManager;
import com.example.alexeyglushkov.taskmanager.task.TaskPool;
import com.example.alexeyglushkov.taskmanager.task.TaskProvider;

/**
 * Created by alexeyglushkov on 04.11.15.
 */
public class ServiceTaskRunner implements ServiceCommandRunner {
    private TaskManager taskManager;
    private TaskProvider taskProvider;

    public ServiceTaskRunner(TaskManager taskManager, String id) {
        this.taskManager = taskManager;
        this.taskProvider = new PriorityTaskProvider(this.taskManager.getHandler(), id);
        this.taskManager.addTaskProvider(this.taskProvider);
    }

    @Override
    public void run(ServiceCommand command) {
        ServiceTask serviceTask = (ServiceTask)command;
        taskProvider.addTask(serviceTask);
    }

    @Override
    public void cancel(ServiceCommand command) {
        ServiceTask serviceTask = (ServiceTask)command;
        taskManager.cancel(serviceTask, null);
    }
}
