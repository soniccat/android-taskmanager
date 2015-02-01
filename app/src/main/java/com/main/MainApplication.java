package com.main;

import com.rssclient.model.RssStorage;
import com.ga.task.SimpleTaskManager;
import com.ga.task.TaskManager;

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
