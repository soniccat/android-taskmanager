package com.example.alexeyglushkov.authtaskmanager

import com.example.alexeyglushkov.authorization.Auth.ServiceCommand
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner
import com.example.alexeyglushkov.taskmanager.TaskManager
import com.example.alexeyglushkov.taskmanager.providers.StackTaskProvider
import com.example.alexeyglushkov.taskmanager.providers.TaskProvider
import com.example.alexeyglushkov.taskmanager.pool.start

/**
 * Created by alexeyglushkov on 04.11.15.
 */
class ServiceTaskRunner(private val taskManager: TaskManager, id: String) : ServiceCommandRunner {
    private val taskProvider: TaskProvider

    override suspend fun <R, C : ServiceCommand<R>> run(command: C): R {
        val serviceTask = command as ServiceTask<*>
        return taskProvider.start(serviceTask.task)
    }

    override fun <T : ServiceCommand<*>> cancel(command: T) {
        val serviceTask = command as ServiceTask<*>
        taskManager.cancel(serviceTask.task, null)
    }

    init {
        taskProvider = StackTaskProvider(true, id, taskManager.threadRunner)
        taskManager.addTaskProvider(taskProvider)
    }
}