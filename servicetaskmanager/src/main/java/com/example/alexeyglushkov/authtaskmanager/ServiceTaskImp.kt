package com.example.alexeyglushkov.authtaskmanager

import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder
import com.example.alexeyglushkov.taskmanager.task.SimpleTask
import com.example.alexeyglushkov.taskmanager.task.Task
import com.example.alexeyglushkov.taskmanager.task.TaskBase
import com.example.alexeyglushkov.taskmanager.task.TaskImpl

/**
 * Created by alexeyglushkov on 17.07.16.
 */
abstract class ServiceTaskImp<T> : BaseServiceTask<T>() {
    init {
        setTask(object : TaskImpl() {
            override suspend fun startTask() {
                onStart()

                private.handleTaskCompletion()
            }
        })
    }

    abstract fun onStart()

    protected fun setResult(result: T) {
        task.private.taskResult = result
    }

    protected fun setError(err: Error) {
        task.private.taskError = err
    }
}
