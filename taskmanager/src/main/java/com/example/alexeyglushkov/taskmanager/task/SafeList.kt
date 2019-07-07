package com.example.alexeyglushkov.taskmanager.task

import android.os.Handler

import com.example.alexeyglushkov.tools.HandlerTools

import java.util.ArrayList

/**
 * Created by alexeyglushkov on 28.05.17.
 */

class SafeList<T>(val originalList: ArrayList<T>,
        // Getters
                  val handler: Handler) : ArrayList<T>() {
    val safeList = ArrayList<T>()

    init {
        fillSafeList()
    }

    // Overrides

    override val size: Int
        get() = originalList.size

    override fun add(element: T): Boolean {
        val result = originalList.add(element)
        fillSafeList()
        return result
    }

    override fun removeAt(index: Int): T {
        val result = originalList.removeAt(index)
        fillSafeList()
        return result
    }

    override fun remove(element: T): Boolean {
        val result = originalList.remove(element)
        fillSafeList()
        return result
    }

    override fun indexOf(element: T): Int {
        return originalList.indexOf(element)
    }

    override fun iterator(): MutableIterator<T> {
        return originalList.iterator()
    }

    // Sync methods

    private fun fillSafeList() {
        HandlerTools.runOnHandlerThread(handler) { fillSafeListOnThread(originalList.clone() as ArrayList<T>) }
    }

    private fun fillSafeListOnThread(list: ArrayList<T>) {
        safeList.clear()
        safeList.addAll(list)
    }
}
