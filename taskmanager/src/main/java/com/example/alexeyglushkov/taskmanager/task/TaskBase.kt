package com.example.alexeyglushkov.taskmanager.task

abstract class TaskBase: Task, TaskPrivate {
    val private: TaskPrivate
        get() = this
}
