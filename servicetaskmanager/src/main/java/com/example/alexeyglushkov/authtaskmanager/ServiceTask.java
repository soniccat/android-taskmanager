package com.example.alexeyglushkov.authtaskmanager;

import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.taskmanager.task.Task;

import androidx.annotation.NonNull;

/**
 * Created by alexeyglushkov on 16.07.16.
 */
interface ServiceTask<T> extends ServiceCommand<T> {
    @NonNull Task getTask();
}
