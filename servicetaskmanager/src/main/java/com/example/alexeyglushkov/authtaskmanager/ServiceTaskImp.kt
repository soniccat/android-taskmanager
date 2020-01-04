package com.example.alexeyglushkov.authtaskmanager

import com.example.alexeyglushkov.taskmanager.task.TaskImpl
import java.lang.Exception

/**
 * Created by alexeyglushkov on 17.07.16.
 */
abstract class ServiceTaskImp<T> : BaseServiceTask<T>() {
    init {
        task = object : TaskImpl() {
            override suspend fun startTask() {
                onStart()
            }
        }
    }

    abstract fun onStart()

    protected fun setResult(result: T) {
        task.private.taskResult = result
    }

    protected fun setError(err: Exception) {
        task.private.taskError = err
    }
}
