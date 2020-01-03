package com.rssclient.rssfeeditems.view

import android.graphics.drawable.BitmapDrawable
import android.view.View
import com.aglushkov.taskmanager_http.image.ImageBinder
import com.example.alexeyglushkov.ext.getDrawableCompat
import com.rssclient.controllers.R
import com.rssclient.model.RssItem

class RssItemBinder(val imageBinder: ImageBinder) {
    companion object {
        val ViewHolderKey = "ViewHolderKey"
        val ImageTagKey = "ImageTag"
    }

    var listener: Listener? = null

    fun bind(item: RssItem, holder: RssItemsAdapter.RssItemsViewHolder) {
        holder.name?.text = item.title
        holder.itemView.setOnClickListener {
            listener?.onClick(item)
        }

        val image = item.image
        val imageView = holder.image

        val imageTag = image?.hashCode().toString()
        holder.progressTag = imageTag.toString()

        val defaultImage = imageView.context.getDrawableCompat(R.drawable.ic_launcher)
        imageBinder.bind(imageView,
                item.image,
                null,
                mapOf( ViewHolderKey to holder,
                       ImageTagKey to imageTag)
        ) { bitmap, e ->
            if (bitmap != null) {
                holder.showImage(BitmapDrawable(imageView.resources, bitmap))
            } else {
                holder.showImage(defaultImage)
            }
        }

        if (image != null) {
            holder.image.visibility = View.INVISIBLE
            holder.progressBar.visibility = View.VISIBLE
            listener?.bindImageProgress(holder, holder.progressTag)

        } else {
            holder.showImage(defaultImage)
        }
    }

    fun clear(holder: RssItemsAdapter.RssItemsViewHolder) {
        listener?.unbindImageProgress(holder.progressTag)
        imageBinder.clear(holder.image)
        holder.progressTag = ""
    }

    interface Listener {
        fun onClick(item: RssItem)

        fun bindImageProgress(viewHolder: RssItemsAdapter.RssItemsViewHolder, imageTag: String)
        fun unbindImageProgress(imageTag: String)
    }
}