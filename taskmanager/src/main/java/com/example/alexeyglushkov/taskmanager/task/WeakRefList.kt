package com.example.alexeyglushkov.taskmanager.task

import java.lang.ref.WeakReference
import java.util.ArrayList

/**
 * Created by alexeyglushkov on 23.08.15.
 */
class WeakRefList<T> : ArrayList<WeakReference<T>>() {

    override fun iterator(): MutableIterator<WeakReference<T>> {
        clean()
        return super.iterator()
    }

    fun clean() {
        var i = this.size
        while (--i >= 0) {
            if (get(i).get() == null) {
                removeAt(i)
            }
        }
    }

    fun containsValue(element: T): Boolean {
        for (ref in this) {
            if (ref.get() === element) {
                return true
            }
        }

        return false
    }

    override fun contains(element: WeakReference<T>): Boolean {
        return super.contains(element)
    }

    fun removeValue(element: T): Boolean {
        var i = 0
        for (ref in this) {
            if (ref.get() === element) {
                removeAt(i)
                return true
            }

            ++i
        }

        return false
    }

    override fun remove(element: WeakReference<T>): Boolean {
        return super.remove(element)
    }

    companion object {
        private val serialVersionUID = 4962762847646156417L
    }
}
