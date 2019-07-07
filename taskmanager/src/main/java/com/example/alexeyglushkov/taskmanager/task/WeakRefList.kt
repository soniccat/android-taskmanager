package com.example.alexeyglushkov.taskmanager.task

import java.lang.ref.WeakReference
import java.util.ArrayList

/**
 * Created by alexeyglushkov on 23.08.15.
 */
class WeakRefList<T> : ArrayList<WeakReference<T>>() {

    override fun iterator(): Iterator<WeakReference<T>> {
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

    override operator fun contains(`object`: Any?): Boolean {
        if (`object` is WeakReference<*>) {
            return super.contains(`object`)
        }

        for (ref in this) {
            if (ref.get() === `object`) {
                return true
            }
        }

        return false
    }

    override fun remove(`object`: Any?): Boolean {
        if (`object` is WeakReference<*>) {
            return super.remove(`object`)
        }

        var i = 0
        for (ref in this) {
            if (ref.get() === `object`) {
                removeAt(i)
                return true
            }

            ++i
        }

        return false
    }

    companion object {

        private val serialVersionUID = 4962762847646156417L
    }
}
