package com.example.alexeyglushkov.taskmanager.ui

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

import androidx.collection.SparseArrayCompat

import com.example.alexeyglushkov.taskmanager.R
import com.example.alexeyglushkov.taskmanager.task.SimpleTaskManagerSnapshot
import com.example.alexeyglushkov.taskmanager.task.TaskManagerSnapshot

import java.util.ArrayList
import java.util.Arrays
import java.util.HashSet

/**
 * Created by alexeyglushkov on 06.01.15.
 */
class TaskManagerView : LinearLayout {
    private var snapshot: TaskManagerSnapshot = SimpleTaskManagerSnapshot()
    private lateinit var barView: TaskBarView
    private lateinit var loadingTasks: TextView
    private lateinit var waitingTasks: TextView
    private var colors = mutableListOf<Int>()
    private var allTypes = listOf<Int>()

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    override fun onFinishInflate() {
        super.onFinishInflate()

        colors.addAll(Arrays.asList(Color.CYAN, Color.GREEN, Color.BLUE, Color.MAGENTA))

        barView = findViewById(R.id.bar)
        loadingTasks = findViewById(R.id.loading)
        waitingTasks = findViewById(R.id.waiting)
    }

    fun showSnapshot(snapshot: TaskManagerSnapshot) {
        this.snapshot = snapshot

        updateAllTypesArray()
        updateLoadingTasks()
        updateWaitingTasks()
        updateBar()
    }

    internal fun updateLoadingTasks() {
        val str = StringBuilder()
        str.append("Loading: ")
        str.append(snapshot.loadingTasksCount.toString() + " ")

        val loadingTaskInfo = snapshot.usedLoadingSpace
        var count: Int
        for (type in allTypes) {
            count = loadingTaskInfo.get(type, 0)
            str.append("$count ")
        }

        loadingTasks.text = str
    }

    internal fun updateWaitingTasks() {
        val str = StringBuilder()
        str.append("Waiting: ")
        str.append(snapshot.waitingTasksCount.toString() + " ")

        val loadingTaskInfo = snapshot.waitingTaskInfo
        var count: Int
        for (type in allTypes) {
            count = loadingTaskInfo.get(type, 0)
            str.append("$count ")
        }

        waitingTasks.text = str
    }

    internal fun updateBar() {
        val maxQueueSize = snapshot.maxQueueSize
        val loadingSpace = snapshot.usedLoadingSpace
        val loadingLimits = snapshot.loadingLimits

        barView.clearItems()

        var count: Int
        var usedSpace: Float
        var reachedLimit = false
        for (type in allTypes) {
            count = loadingSpace.get(type, 0)
            usedSpace = count.toFloat() / maxQueueSize.toFloat()
            reachedLimit = false
            if (loadingLimits.get(type, -1.0f) != -1.0f) {
                reachedLimit = usedSpace >= snapshot.loadingLimits.get(type, 0.0f)
            }

            val item = TaskBarView.TaskBarItem(type, count.toFloat() / maxQueueSize.toFloat(), getColor(type), reachedLimit)
            barView.addItem(item)
        }
    }

    private fun getColor(index: Int): Int {
        return if (index < colors.size) {
            colors[index]
        } else {
            Color.BLACK
        }
    }

    private fun updateAllTypesArray() {
        val allTypesSet = HashSet<Int>()

        val loadingTaskInfo = snapshot.usedLoadingSpace
        val loadingLimits = snapshot.loadingLimits
        val waitingTaskInfo = snapshot.waitingTaskInfo

        //fill types array
        var type: Int
        for (i in 0 until loadingTaskInfo.size()) {
            type = loadingTaskInfo.keyAt(i)
            allTypesSet.add(type)
        }

        for (i in 0 until loadingLimits.size()) {
            type = loadingLimits.keyAt(i)
            allTypesSet.add(type)
        }

        for (i in 0 until waitingTaskInfo.size()) {
            type = waitingTaskInfo.keyAt(i)
            allTypesSet.add(type)
        }

        val allTypesArray = arrayOfNulls<Int>(allTypesSet.size)
        allTypesSet.toTypedArray<Int>()
        Arrays.sort(allTypesArray)
        allTypes = Arrays.asList<Int>(*allTypesArray)
    }
}
