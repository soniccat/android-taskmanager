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

    Executor executor;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
        }
    };

    // TODO: BlockingQueue gives bad performance, consider using our own executor
    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<Runnable>(128);

    public SimpleTaskExecutor() {
        // TODO: need to transfer these values somehow
        // they should depend on task manager settings
        executor = new ThreadPoolExecutor(10, 10, 60, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);
    }

    @Override
    public void executeTask(final Task task) {
        final Task.Callback callback = task.getTaskCallback();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (!Tasks.isTaskCompleted(task)) { // the task could be already handled by task manager
                    task.startTask(callback);
                }
            }
        });
    }
}
