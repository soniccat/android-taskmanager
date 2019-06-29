package com.example.alexeyglushkov.taskmanager.task;

import android.util.SparseArray;

import androidx.collection.SparseArrayCompat;

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
    SparseArrayCompat<Float> getLoadingLimits();
    SparseArrayCompat<Integer> getUsedLoadingSpace();
    SparseArrayCompat<Integer> getWaitingTaskInfo();

    interface OnSnapshotChangedListener {
        void onSnapshotChanged(TaskManagerSnapshot snapshot);
    }
}
