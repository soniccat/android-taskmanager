package com.example.alexeyglushkov.taskmanager.tools

import java.util.ArrayList
import java.util.Collections
import java.util.Comparator

/**
 * Created by alexeyglushkov on 20.09.15.
 */
class SortedList<T>(private val comparator: Comparator<T>) : ArrayList<T>() {

    override fun add(element: T): Boolean {
        return addInSortedOrder(element)
    }

    fun addInSortedOrder(elem: T): Boolean {
        var insertIndex = Collections.binarySearch(this, elem, comparator)
        if (insertIndex < 0) {
            insertIndex = -insertIndex - 1
        } else {
            // put at the right position
            ++insertIndex
            for (i in insertIndex until size) {
                if (comparator.compare(get(i), elem) != 0) {
                    break
                } else {
                    ++insertIndex
                }
            }
        }

        super.add(insertIndex, elem)
        return true
    }

    fun updateSortedOrder() {
        Collections.sort(this, comparator)
    }

    companion object {
        private val serialVersionUID = 3697965950633119964L
    }
}
