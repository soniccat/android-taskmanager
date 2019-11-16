package com.example.alexeyglushkov.taskmanager.task

import android.os.Handler

import com.example.alexeyglushkov.tools.HandlerTools
import kotlinx.coroutines.launch

import java.util.ArrayList

/**
 * Created by alexeyglushkov on 15.08.15.
 */
object TaskPools {

    // TODO: I am not sure that these methods are necessary
    // maybe it's better to move them in Tools file
    fun getFilteredTasks(taskPool: TaskPool, filter: TaskFilter, completion: FilterCompletion) {
        taskPool.threadRunner.launch {
            val tasks = ArrayList(taskPool.getTasks())
            HandlerTools.runOnHandlerThread(filter.handler) {
                val filteredTasks = ArrayList<Task>()
                for (task in tasks) {
                    if (filter.keepTask(task)) {
                        filteredTasks.add(task)
                    }
                }

                HandlerTools.runOnHandlerThread(completion.handler) { completion.completed(filteredTasks) }
            }
        }
    }

    fun applyTransformation(tasks: List<Task>, trasformation: TaskTransformation) {
        for (task in tasks) {
            trasformation.apply(task)
        }
    }

    interface TaskFilter {
        val handler: Handler
        fun keepTask(task: Task): Boolean
    }

    interface FilterCompletion {
        val handler: Handler
        fun completed(tasks: List<Task>)
    }

    interface TaskTransformation {
        fun apply(task: Task)
    }
}
