package com.rssclient.rssfeeditems.view

import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.LiveData
import com.aglushkov.taskmanager_http.image.Image
import com.aglushkov.taskmanager_http.image.ImageBinder
import com.example.alexeyglushkov.ext.getDrawableCompat
import com.rssclient.controllers.R
import com.rssclient.model.RssItem
import java.net.URL

class RssItemBinder(val imageBinder: ImageBinder) {
    companion object {
        val CellViewKey = "CellViewKey"
        val ImageTag = "ImageTag"
    }

    var listener: Listener? = null

    fun bind(item: RssItem, holder: RssItemsAdapter.RssItemsViewHolder) {
        holder.name?.text = item.title
        holder.itemView.setOnClickListener {
            listener?.onClick(item)
        }

        val image = item.image
        val imageView = holder.image

        val imageTag = image?.hashCode() ?: 0
        holder.progressTag = imageTag

        val placeholder = imageView.context.getDrawableCompat(R.drawable.ic_launcher)
        imageBinder.bind(imageView, item.image, placeholder, mapOf(
                CellViewKey to holder.itemView,
                ImageTag to imageTag as Any))

        if (image != null) {
            holder.progressBar.visibility = View.VISIBLE
            listener?.bindImageProgress(holder.progressBar, holder.progressTag)
        } else {
            holder.progressBar.visibility = View.INVISIBLE
        }
    }

    fun clear(holder: RssItemsAdapter.RssItemsViewHolder) {
        listener?.unbindImageProgress(holder.progressTag)
        imageBinder.clear(holder.image)
        holder.progressTag = 0
    }

    interface Listener {
        fun onClick(item: RssItem)

        fun bindImageProgress(progressBar: ProgressBar, tag: Int)
        fun unbindImageProgress(tag: Int)
    }
}