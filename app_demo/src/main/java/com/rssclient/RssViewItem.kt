package com.rssclient

import androidx.recyclerview.widget.DiffUtil
import com.rssclient.model.RssFeed
import com.rssclient.model.RssItem
import java.util.*

sealed class RssViewItem<T> {
    var type = 0
    var items = listOf<T>()

    fun firstItem(): T = items.first()

    abstract fun equalsByIds(item: RssViewItem<*>): Boolean

    constructor(item: T, type: Int) {
        this.items = Collections.singletonList(item)
        this.type = type
    }

    constructor(items: List<T>, type: Int) {
        this.items = items
        this.type = type
    }

    class RssFeedViewItem(feed: RssFeed): RssViewItem<RssFeed>(feed, Type) {
        companion object {
            const val Type = 1
        }

        override fun equalsByIds(item: RssViewItem<*>): Boolean {
            return type == item.type && item is RssFeedViewItem && firstItem().url == item.firstItem().url
        }
    }

    class RssItemViewItem(item: RssItem): RssViewItem<RssItem>(item, Type) {
        companion object {
            const val Type = 2
        }

        override fun equalsByIds(item: RssViewItem<*>): Boolean {
            return type == item.type && item is RssItemViewItem && firstItem().link == item.firstItem().link
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RssViewItem<*>

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
        @JvmStatic val DiffCallback = object : DiffUtil.ItemCallback<RssViewItem<*>>() {
            override fun areItemsTheSame(oldCellInfo: RssViewItem<*>, newCellInfo: RssViewItem<*>): Boolean {
                return oldCellInfo.equalsByIds(newCellInfo)
            }

            override fun areContentsTheSame(oldCellInfo: RssViewItem<*>, newCellInfo: RssViewItem<*>): Boolean {
                return oldCellInfo == newCellInfo
            }
        }
    }
}