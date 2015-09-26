package com.taskmanager.task;

import android.util.SparseArray;

/**
 * Created by alexeyglushkov on 23.08.15.
 */
public interface TaskManagerSnapshot {

    void startSnapshotRecording(TaskManager taskManager);
    void stopSnapshotRecording();

    void addSnapshotListener(OnSnapshotChangedListener listener);
    void removeSnapshotListener(OnSnapshotChangedListener listener);

    int getLoadingTasksCount();
    int getWaitingTasksCount();
    int getMaxQueueSize();
    SparseArray<Float> getLoadingLimits();
    SparseArray<Integer> getUsedLoadingSpace();
    SparseArray<Integer> getWaitingTaskInfo();

    interface OnSnapshotChangedListener {
        void onSnapshotChanged(TaskManagerSnapshot snapshot);
    }
}
