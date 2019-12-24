package com.rssclient.controllers

import android.content.Context
import android.graphics.Bitmap
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aglushkov.taskmanager_http.image.Image
import com.aglushkov.taskmanager_http.image.ImageLoader
import com.aglushkov.taskmanager_http.image.ImageLoader.LoadCallback
import com.aglushkov.taskmanager_http.image.ImageLoader.loadImage
import com.example.alexeyglushkov.taskmanager.task.Task
import com.example.alexeyglushkov.taskmanager.task.TaskManager
import com.rssclient.model.RssFeed
import com.rssclient.vm.RssItemView
import kotlinx.android.synthetic.main.feed_cell.view.*

class FeedsAdapter(private val taskManager: TaskManager)
    : ListAdapter<RssItemView<*>, RecyclerView.ViewHolder>(DiffCallback) {

    companion object {
        @JvmStatic val DiffCallback = object : DiffUtil.ItemCallback<RssItemView<*>>() {
            override fun areItemsTheSame(oldCellInfo: RssItemView<*>, newCellInfo: RssItemView<*>): Boolean {
                return oldCellInfo.equalsByIds(newCellInfo)
            }

            override fun areContentsTheSame(oldCellInfo: RssItemView<*>, newCellInfo: RssItemView<*>): Boolean {
                return oldCellInfo == newCellInfo
            }
        }
    }

    private val loadingImages = SparseArray<Image>()

    var listener: FeedsAdapterListener? = null

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            RssItemView.RssFeedView.Type -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.feed_cell, parent, false)
                RssFeedViewHolder(view)
            }
            else -> throw IllegalArgumentException("FeedsAdapter.onCreateViewHolder: viewType is ${viewType}")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = getItem(position)
        when (data) {
            is RssItemView.RssFeedView -> {
                holder as RssFeedViewHolder

                val item = data.firstItem()
                holder.name?.text = item.name
                holder.itemView.tag = item.hashCode()
            }
        }
    }

    internal fun loadImage(image: Image?, holder: ViewHolder) {
        holder.imageView!!.setImageBitmap(null)
        holder.loadingImage = image
        if (image == null) {
            return
        }
        val position = holder.position
        val task = ImageLoader().loadImage(taskManager.threadRunner, image, Integer.toString(holder.hashCode()), object : LoadCallback {
            override fun completed(task: Task?, image: Image?, bitmap: Bitmap?, error: Error?) {
                val view = listener!!.getViewAtPosition(position)
                if (view != null) {
                    val holder = view.tag as ViewHolder
                    if (holder.loadingImage === image) {
                        if (bitmap != null) {
                            holder.imageView!!.setImageBitmap(bitmap)
                        }
                        holder.loadingImage = null
                        //TODO: hide progress bar
                    }
                }
            }
        })
        taskManager.addTask(task)
    }

    class RssFeedViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var name: TextView? = null
        var image: ImageView? = null

        init {
            name = view.name // TODO: switch to Google bindings instead of synthetic
            image = view.icon
        }
    }

    interface FeedsAdapterListener {
        fun getViewAtPosition(position: Int): View?
    }
}