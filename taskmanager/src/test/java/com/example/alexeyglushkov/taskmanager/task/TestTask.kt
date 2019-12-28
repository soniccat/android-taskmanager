package com.example.alexeyglushkov.taskmanager.task

import android.util.Log

/**
 * Created by alexeyglushkov on 09.08.15.
 */
open class TestTask : SimpleTask() {

    override suspend fun startTask() {
        Log.d("com.example.alexeyglushkov.taskmanager.task.TestTask", "startTask " + finishCallback)
    }

    fun finish() {
        Log.d("com.example.alexeyglushkov.taskmanager.task.TestTask", "finish " + finishCallback)
    }
}
