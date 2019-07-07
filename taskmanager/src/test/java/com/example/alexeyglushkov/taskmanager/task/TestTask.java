package com.example.alexeyglushkov.taskmanager.task;

import android.util.Log;

import com.example.alexeyglushkov.taskmanager.task.SimpleTask;
import com.example.alexeyglushkov.taskmanager.task.Task;

/**
 * Created by alexeyglushkov on 09.08.15.
 */
public class TestTask extends SimpleTask {

    @Override
    public void startTask() {
        super.startTask();
        Log.d("com.example.alexeyglushkov.taskmanager.task.TestTask", "startTask " + getStartCallback());
    }

    public void finish() {
        Log.d("com.example.alexeyglushkov.taskmanager.task.TestTask", "finish " + getStartCallback());
        getPrivate().handleTaskCompletion();
    }
}
