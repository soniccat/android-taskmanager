package com.example.alexeyglushkov.taskmanager.task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by alexeyglushkov on 08.02.15.
 */
public class SimpleTaskExecutor implements TaskExecutor {

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
        }
    };

    public SimpleTaskExecutor() {
    }

    @Override
    public void executeTask(final Task task) {
        final Task.Callback callback = task.getTaskCallback();
        final Thread thread = sThreadFactory.newThread(new Runnable() {
            @Override
            public void run() {
                if (!Tasks.isTaskCompleted(task)) { // the task could be already handled by task manager
                    task.startTask(callback);
                }
            }
        });

        thread.start();
    }
}
