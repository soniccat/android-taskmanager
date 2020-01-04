package com.rssclient.rssfeed.view

import com.aglushkov.taskmanager_http.image.ImageBinder
import com.example.alexeyglushkov.ext.getDrawableCompat
import com.rssclient.controllers.R
import com.rssclient.model.RssFeed

class RssFeedBinder(val imageBinder: ImageBinder) {
    var listener: Listener? = null

    fun bind(feed: RssFeed, holder: RssFeedsAdapter.RssFeedViewHolder) {
        holder.name?.text = feed.name

        holder.itemView.setOnClickListener {
            listener?.onClick(feed)
        }

        holder.itemView.setOnLongClickListener {
            listener?.onLongPressed(feed)
            return@setOnLongClickListener true
        }

        val placeholder = holder.itemView.context.getDrawableCompat(R.drawable.ic_launcher)
        imageBinder.bind(holder.image!!, feed.image, placeholder)
    }

    fun clear(holder: RssFeedsAdapter.RssFeedViewHolder) {
        imageBinder.clear(holder.image)
    }

    interface Listener {
        fun onClick(feed: RssFeed)
        fun onLongPressed(feed: RssFeed)
    }
}