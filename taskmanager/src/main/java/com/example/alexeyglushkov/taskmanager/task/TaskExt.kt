package com.example.alexeyglushkov.taskmanager.task

import android.util.Log

fun Task.isReadyToStart(): Boolean {
    val st = taskStatus
    return st == Task.Status.NotStarted ||
            st == Task.Status.Waiting
}

fun Task.isBlocked(): Boolean {
    return taskStatus == Task.Status.Blocked || hasActiveDependencies()
}

fun Task.isFinished(): Boolean {
    val st = taskStatus
    return st == Task.Status.Completed || st == Task.Status.Cancelled
}

fun Task.log(tag: String, prefix: String) {
    Log.d(tag, "$prefix " +
            "[$javaClass ($taskStatus) " +
            "id= $taskId " +
            "priority= $taskPriority " +
            "type= $taskType " +
            "time= ${taskDuration()}] " +
            "$task")
}