package com.example.alexeyglushkov.taskmanager.task

import android.util.Log

import com.example.alexeyglushkov.taskmanager.task.SimpleTask
import com.example.alexeyglushkov.taskmanager.task.Task

/**
 * Created by alexeyglushkov on 09.08.15.
 */
open class TestTask : SimpleTask() {

    override fun startTask() {
        super.startTask()
        Log.d("com.example.alexeyglushkov.taskmanager.task.TestTask", "startTask " + startCallback!!)
    }

    fun finish() {
        Log.d("com.example.alexeyglushkov.taskmanager.task.TestTask", "finish " + startCallback!!)
        private.handleTaskCompletion()
    }
}
