package com.example.alexeyglushkov.taskmanager.task

class NullTopTaskResolver: TaskProvider.TopTaskResolver {
    override fun getTopTask(): Task? {
        return null;
    }

    override fun takeTopTask(): Task? {
        return null;
    }
}