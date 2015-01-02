package com.example.rssreader;

import com.ga.rss.RssStorage;
import com.ga.task.SimpleTaskManager;
import com.ga.task.TaskManager;

import android.app.Application;

public class RssApplication extends Application {
    TaskManager loadManager;
    TaskManager keepManager;
    RssStorage rssStorage;

    public RssApplication() {
        super();
        loadManager = new SimpleTaskManager(10);
        keepManager = new SimpleTaskManager(10);
        rssStorage = new RssStorage("RssStorage");
    }

    public TaskManager loader() {
        return loadManager;
    }

    public TaskManager keeper() {
        return keepManager;
    }

    public RssStorage rssStorage() {
        return rssStorage;
    }
}
