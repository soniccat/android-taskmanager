package com.example.alexeyglushkov.taskmanager.task

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class TaskProviderDispatcher(val taskProvider: TaskProvider, val taskWrapper: TaskWrapper): CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        val task = taskWrapper.wrap(block)
        taskProvider.addTask(task)
    }

    interface TaskWrapper {
        fun wrap(runnable: Runnable): Task
    }
}