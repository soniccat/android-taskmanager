package com.aglushkov.modelcore.view

import androidx.recyclerview.widget.DiffUtil
import java.util.*

abstract class BaseViewItem<T> {
    var type = 0
    var items = listOf<T>()

    constructor(item: T, type: Int) {
        this.items = Collections.singletonList(item)
        this.type = type
    }

    constructor(items: List<T>, type: Int) {
        this.items = items
        this.type = type
    }

    fun firstItem() = items.first()

    abstract fun equalsByIds(item: BaseViewItem<*>): Boolean

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BaseViewItem<*>

        if (type != other.type) return false
        if (items != other.items) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type
        result = 31 * result + items.hashCode()
        return result
    }

    companion object {
        // ListAdapter diff callback
        @JvmStatic val DiffCallback = object : DiffUtil.ItemCallback<BaseViewItem<*>>() {
            override fun areItemsTheSame(oldCellInfo: BaseViewItem<*>, newCellInfo: BaseViewItem<*>): Boolean {
                return oldCellInfo.equalsByIds(newCellInfo)
            }

            override fun areContentsTheSame(oldCellInfo: BaseViewItem<*>, newCellInfo: BaseViewItem<*>): Boolean {
                return oldCellInfo == newCellInfo
            }
        }
    }
}