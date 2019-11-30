package com.example.alexeyglushkov.taskmanager.task

import android.util.Log

import com.example.alexeyglushkov.taskmanager.task.SimpleTask
import com.example.alexeyglushkov.taskmanager.task.Task

/**
 * Created by alexeyglushkov on 09.08.15.
 */
open class TestTask : SimpleTask() {

    override suspend fun startTask() {
        Log.d("com.example.alexeyglushkov.taskmanager.task.TestTask", "startTask " + taskCallback)
    }

    fun finish() {
        Log.d("com.example.alexeyglushkov.taskmanager.task.TestTask", "finish " + taskCallback)
        private.handleTaskCompletion()
    }
}
