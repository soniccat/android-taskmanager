package com.example.alexeyglushkov.taskmanager.providers

import android.util.Log
import com.example.alexeyglushkov.taskmanager.task.Task
import com.example.alexeyglushkov.taskmanager.task.TaskBase
import com.example.alexeyglushkov.taskmanager.task.isReadyToStart

object TaskProviders {
    // TODO: improve this solution not to have this static method
    // we need to have this call in task providers but not in task pools
    fun addTaskCheck(task: TaskBase, tag: String): Boolean {
        if (!task.isReadyToStart()) {
            Log.d(tag, "Can't put task " + task.javaClass.toString() + " because it has been added " + task.taskStatus.toString())
            return false
        }

        // TaskPool must set Waiting status on the current thread
        task.private.taskStatus = Task.Status.Waiting
        return true
    }
}