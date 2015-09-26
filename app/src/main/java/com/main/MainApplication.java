package com.main;

import com.rssclient.model.RssStorage;
import com.example.alexeyglushkov.taskmanager.task.SimpleTaskManager;
import com.example.alexeyglushkov.taskmanager.task.TaskManager;

import android.app.Application;

public class MainApplication extends Application {
    TaskManager taskManager;
    RssStorage rssStorage;

    public MainApplication() {
        super();
        taskManager = new SimpleTaskManager(10);
        rssStorage = new RssStorage("RssStorage");
    }

    public RssStorage getRssStorage() {
        return rssStorage;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }
}
