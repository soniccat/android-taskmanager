package com.example.alexeyglushkov.taskmanager.task

abstract class TaskBase: Task, TaskPrivate {
    open val private: TaskPrivate
        get() = this
}
