package com.taskmanager.task;

import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;

import java.lang.ref.WeakReference;

/**
 * Created by alexeyglushkov on 23.08.15.
 */
public class SimpleTaskManagerSnapshot implements TaskManagerSnapshot, TaskManager.TaskManagerListener {
    private Handler callbackHandler;

    private int loadingTaskCount;
    private int waitingTaskCount;
    private int maxQueueSize;
    private SparseArray<Float> loadingLimits;
    private SparseArray<Integer> usedLoadingSpace;
    private SparseArray<Integer> waitingTaskInfo;

    private boolean needUpdateSnapshot;
    private WeakRefList<OnSnapshotChangedListener> snapshotChangedListeners;

    public SimpleTaskManagerSnapshot() {
        callbackHandler = new Handler(Looper.myLooper());
        snapshotChangedListeners = new WeakRefList<OnSnapshotChangedListener>();
    }

    private void bind(final TaskManager taskManager) {
        Tools.runOnHandlerThread(taskManager.getHandler(), new Runnable() {
            @Override
            public void run() {
                bindOnThread(taskManager);

                Tools.runOnHandlerThread(callbackHandler, new Runnable() {
                    @Override
                    public void run() {
                        triggerOnSnapshotListeners();
                    }
                });
            }
        });
    }

    private void bindOnThread(TaskManager taskManager) {
        loadingTaskCount = taskManager.getLoadingTaskCount();

        waitingTaskInfo = new SparseArray<Integer>();
        for (TaskProvider taskProvider : taskManager.getTaskProviders()) {
            waitingTaskCount += taskProvider.getTaskCount();

            for (Task task : taskProvider.getTasks()) {
                int count = waitingTaskInfo.get(task.getTaskType(), 0);
                ++count;
                waitingTaskInfo.put(task.getTaskType(), count);
            }
        }

        maxQueueSize = taskManager.getMaxLoadingTasks();
        loadingLimits = taskManager.getLimits();
        usedLoadingSpace = taskManager.getUsedSpace();

        taskManager.addListener(this);
    }

    /*
    public void setMaxQueueSize(int count) {
        maxQueueSize = count;
        triggerOnSnapshotListeners();
    }*/

    public void setLoadingLimit(int taskType, float availableQueuePart) {
        if (availableQueuePart == -1.0f) {
            loadingLimits.remove(taskType);
        } else {
            loadingLimits.put(taskType, availableQueuePart);
        }

        triggerOnSnapshotListeners();
    }

    private void updateUsedLoadingSpace(int taskType, boolean add) {
        Integer count = usedLoadingSpace.get(taskType, 0);
        if (add) {
            ++count;
            ++loadingTaskCount;
        } else {
            --count;
            --loadingTaskCount;

            if (count < 0) {
                count = 0;
            }

            if (loadingTaskCount < 0) {
                loadingTaskCount = 0;
            }
        }

        usedLoadingSpace.put(taskType, count);
        triggerOnSnapshotListeners();
    }

    private void updateWaitingTaskInfo(int taskType, boolean add) {
        int count = waitingTaskInfo.get(taskType, 0);
        if (add) {
            ++waitingTaskCount;
            ++count;
        } else {
            --waitingTaskCount;
            --count;

            if (count < 0) {
                count = 0;
            }

            if (waitingTaskCount < 0) {
                waitingTaskCount = 0;
            }
        }

        waitingTaskInfo.put(taskType, count);
        triggerOnSnapshotListeners();
    }

    void triggerOnSnapshotListeners() {
        for (WeakReference<OnSnapshotChangedListener> listenerRef : snapshotChangedListeners) {
            listenerRef.get().onSnapshotChanged(this);
        }
    }

    @Override
    public void startSnapshotRecording(TaskManager taskManager) {
        if (needUpdateSnapshot) {
            return;
        }

        needUpdateSnapshot = true;
        bind(taskManager);
    }

    @Override
    public void stopSnapshotRecording() {
        if (!needUpdateSnapshot) {
            return;
        }

        needUpdateSnapshot = false;
    }

    @Override
    public void addSnapshotListener(OnSnapshotChangedListener listener) {
        snapshotChangedListeners.add(new WeakReference<OnSnapshotChangedListener>(listener));
    }

    @Override
    public void removeSnapshotListener(OnSnapshotChangedListener listener) {
        int i = 0;
        for (WeakReference<OnSnapshotChangedListener> listenerRef : snapshotChangedListeners) {
            if (listenerRef.get() != null) {
                snapshotChangedListeners.remove(i);
                break;
            }

            ++i;
        }
    }

    // TaskPool.Listener

    @Override
    public void onLimitsChanged(TaskManager taskManager, final int taskType, final float availableQueuePart) {
        Tools.runOnHandlerThread(callbackHandler, new Runnable() {
            @Override
            public void run() {
                setLoadingLimit(taskType, availableQueuePart);
            }
        });
    }

    @Override
    public void onTaskAdded(TaskPool pool, final Task task, final boolean isLoadingQueue) {
        Tools.runOnHandlerThread(callbackHandler, new Runnable() {
            @Override
            public void run() {
                if (isLoadingQueue) {
                    updateUsedLoadingSpace(task.getTaskType(), true);
                } else {
                    updateWaitingTaskInfo(task.getTaskType(), true);
                }
            }
        });
    }

    @Override
    public void onTaskRemoved(TaskPool pool, final Task task, final boolean isLoadingQueue) {
        Tools.runOnHandlerThread(callbackHandler, new Runnable() {
            @Override
            public void run() {
                if (isLoadingQueue) {
                    updateUsedLoadingSpace(task.getTaskType(), false);
                } else {
                    updateWaitingTaskInfo(task.getTaskType(), false);
                }
            }
        });
    }

    //public

    @Override
    public int getLoadingTasksCount() {
        return loadingTaskCount;
    }

    @Override
    public int getWaitingTasksCount() {
        return waitingTaskCount;
    }

    @Override
    public int getMaxQueueSize() {
        return maxQueueSize;
    }

    @Override
    public SparseArray<Float> getLoadingLimits() {
        return loadingLimits;
    }

    @Override
    public SparseArray<Integer> getUsedLoadingSpace() {
        return usedLoadingSpace;
    }

    @Override
    public SparseArray<Integer> getWaitingTaskInfo() {
        return waitingTaskInfo;
    }
}
