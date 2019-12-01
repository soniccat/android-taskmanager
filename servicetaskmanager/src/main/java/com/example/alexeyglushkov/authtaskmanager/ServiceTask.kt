package com.example.alexeyglushkov.authtaskmanager

import com.example.alexeyglushkov.authorization.Auth.ServiceCommand
import com.example.alexeyglushkov.taskmanager.task.Task

/**
 * Created by alexeyglushkov on 16.07.16.
 */
internal interface ServiceTask<T> : ServiceCommand<T> {
    val task: Task
}