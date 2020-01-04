package com.example.alexeyglushkov.taskmanager.tools

import android.os.Handler
import android.os.Looper

import com.example.alexeyglushkov.tools.HandlerTools

import java.util.ArrayList

/**
 * Created by alexeyglushkov on 28.05.17.
 */

class SafeList<T>(val originalList: ArrayList<T>,
                  val safeHandler: Handler) : ArrayList<T>() {
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

    override fun get(index: Int): T {
        if (Looper.myLooper() == safeHandler.looper) {
            return safeList[index]
        } else {
            return originalList[index]
        }
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
        HandlerTools.runOnHandlerThread(safeHandler) { fillSafeListOnThread(originalList.clone() as ArrayList<T>) }
    }

    private fun fillSafeListOnThread(list: ArrayList<T>) {
        safeList.clear()
        safeList.addAll(list)
    }
}
