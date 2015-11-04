package com.example.alexeyglushkov.authtaskmanager;

import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner;
import com.example.alexeyglushkov.taskmanager.task.PriorityTaskProvider;
import com.example.alexeyglushkov.taskmanager.task.TaskPool;
import com.example.alexeyglushkov.taskmanager.task.TaskProvider;

/**
 * Created by alexeyglushkov on 04.11.15.
 */
public class ServiceTaskRunner implements ServiceCommandRunner {
    TaskProvider taskProvider = new PriorityTaskProvider();

    @Override
    public void run(ServiceCommand command) {

    }

    @Override
    public void cancel(ServiceCommand command) {

    }
}
