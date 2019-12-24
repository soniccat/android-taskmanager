package com.rssclient.vm

import com.rssclient.model.RssFeed
import java.util.*

sealed class RssItemView<T> {
    var type = 0
    var items = listOf<T>()

    fun firstItem(): T = items.first()

    abstract fun equalsByIds(item: RssItemView<*>): Boolean

    constructor(item: T, type: Int) {
        this.items = Collections.singletonList(item)
        this.type = type
    }

    constructor(items: List<T>, type: Int) {
        this.items = items
        this.type = type
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RssItemView<*>

        if (type != other.type) return false
        if (items != other.items) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type
        result = 31 * result + items.hashCode()
        return result
    }

    class RssFeedView(feed: RssFeed): RssItemView<RssFeed>(feed, Type) {
        companion object {
            const val Type = 1
        }

        override fun equalsByIds(item: RssItemView<*>): Boolean {
            return type == item.type && item is RssFeedView && firstItem().url == item.firstItem().url
        }
    }
}