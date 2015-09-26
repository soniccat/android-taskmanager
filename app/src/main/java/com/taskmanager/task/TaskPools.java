package com.taskmanager.task;

import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexeyglushkov on 15.08.15.
 */
public class TaskPools {

    // TODO: I am not sure that these methods are necessary
    // maybe it's better to move them in Tools file
    public static void getFilteredTasks(final TaskPool taskPool, final TaskFilter filter, final FilterCompletion completion) {
        Tools.runOnHandlerThread(taskPool.getHandler(), new Runnable() {
            @Override
            public void run() {
                final List<Task> tasks = new ArrayList<Task>(taskPool.getTasks());
                Tools.runOnHandlerThread(filter.getHandler(), new Runnable() {
                    @Override
                    public void run() {
                        final List<Task> filteredTasks = new ArrayList<Task>();
                        for (Task task : tasks) {
                            if (filter.keepTask(task)) {
                                filteredTasks.add(task);
                            }
                        }

                        Tools.runOnHandlerThread(completion.getHandler(), new Runnable() {
                            @Override
                            public void run() {
                                completion.completed(filteredTasks);
                            }
                        });
                    }
                });
            }
        });
    }

    public static void applyTransformation(List<Task> tasks, TaskTransformation trasformation) {
        for (Task task : tasks) {
            trasformation.apply(task);
        }
    }

    interface TaskFilter {
        boolean keepTask(Task task);
        Handler getHandler();
    }

    interface FilterCompletion {
        void completed(List<Task> tasks);
        Handler getHandler();
    }

    interface TaskTransformation {
        void apply(Task task);
    }
}
