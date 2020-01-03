package com.example.alexeyglushkov.taskmanager.task

import android.util.SparseArray

import androidx.collection.SparseArrayCompat

/**
 * Created by alexeyglushkov on 23.08.15.
 */
interface TaskManagerSnapshot {
    val loadingTasksCount: Int
    val waitingTasksCount: Int
    val blockedTasksCount: Int
    val maxQueueSize: Int
    val loadingLimits: SparseArrayCompat<Float>
    val usedLoadingSpace: SparseArrayCompat<Int>
    val waitingTaskInfo: SparseArrayCompat<Int>
    val blockedTaskInfo: SparseArrayCompat<Int>

    fun startSnapshotRecording(taskManager: TaskManager)
    fun stopSnapshotRecording()

    fun addSnapshotListener(listener: OnSnapshotChangedListener)
    fun removeSnapshotListener(listener: OnSnapshotChangedListener)

    interface OnSnapshotChangedListener {
        fun onSnapshotChanged(snapshot: TaskManagerSnapshot)
    }
}
