package com.rssclient.rssfeeditems.view

import com.aglushkov.taskmanager_http.image.ImageBinder
import com.example.alexeyglushkov.ext.getDrawableCompat
import com.rssclient.controllers.R
import com.rssclient.model.RssItem
import com.rssclient.rssfeed.view.RssFeedsAdapter

class RssItemBinder(val imageBinder: ImageBinder) {
    var listener: Listener? = null

    fun bind(item: RssItem, holder: RssFeedsAdapter.RssFeedViewHolder) {
        holder.name?.text = item.title

        holder.itemView.setOnClickListener {
            listener?.onClick(item)
        }

        val placeholder = holder.itemView.context.getDrawableCompat(R.drawable.ic_launcher)
        imageBinder.bind(holder.image!!, item.image, placeholder)
    }

    fun clear(holder: RssFeedsAdapter.RssFeedViewHolder) {
        imageBinder.clear(holder.image)
    }

    interface Listener {
        fun onClick(item: RssItem)
    }
}