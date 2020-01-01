package com.rssclient.rssfeeditems.view

import android.view.View
import com.aglushkov.taskmanager_http.image.ImageBinder
import com.example.alexeyglushkov.ext.getDrawableCompat
import com.rssclient.controllers.R
import com.rssclient.model.RssItem

class RssItemBinder(val imageBinder: ImageBinder) {
    var listener: Listener? = null

    fun bind(item: RssItem, holder: RssItemsAdapter.RssItemsViewHolder) {
        holder.name?.text = item.title

        holder.itemView.setOnClickListener {
            listener?.onClick(item)
        }

        val placeholder = holder.itemView.context.getDrawableCompat(R.drawable.ic_launcher)
        imageBinder.bind(holder.image!!, item.image, placeholder)
        holder.progressBar.visibility = if (holder.image != null) View.VISIBLE else View.INVISIBLE
    }

    fun clear(holder: RssItemsAdapter.RssItemsViewHolder) {
        imageBinder.clear(holder.image)
    }

    interface Listener {
        fun onClick(item: RssItem)
    }
}