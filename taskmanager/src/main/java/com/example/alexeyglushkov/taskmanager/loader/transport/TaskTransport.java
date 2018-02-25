package com.example.alexeyglushkov.taskmanager.loader.transport;

import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;

/**
 * Created by alexeyglushkov on 28.01.18.
 */

public interface TaskTransport {
    //void setContext(TaskTransportContext context);

    String getId();
    void start();
    void cancel();
    Listener getListener();
    void setListener(Listener listener);
    ProgressUpdater getProgressUpdater();

    Object getData();
    Error getError();
    boolean isCancelled();

    interface Listener {
        ProgressUpdater getProgressUpdater(TaskTransport transport, float size);
        boolean needCancel(TaskTransport transport);
    }
}
