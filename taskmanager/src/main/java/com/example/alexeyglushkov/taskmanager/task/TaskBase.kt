package com.example.alexeyglushkov.taskmanager.task

abstract class TaskBase: Task {
    open val private: TaskPrivate
        get() = this as TaskPrivate // force accessing private API via a property
}
