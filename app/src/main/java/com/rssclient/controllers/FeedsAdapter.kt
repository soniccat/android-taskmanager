package com.rssclient.controllers

import android.content.Context
import android.graphics.Bitmap
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.aglushkov.taskmanager_http.image.Image
import com.aglushkov.taskmanager_http.image.ImageLoader.LoadCallback
import com.aglushkov.taskmanager_http.image.ImageLoader.loadImage
import com.example.alexeyglushkov.taskmanager.task.Task
import com.example.alexeyglushkov.taskmanager.task.TaskManager
import com.rssclient.model.RssFeed

class FeedsAdapter(
        context: Context,
        private val values: ArrayList<RssFeed>,
        private val taskManager: TaskManager) : ArrayAdapter<RssFeed>(context, R.layout.feed_cell, values) {
    private val loadingImages: SparseArray<Image>
    var listener: FeedsAdapterListener? = null

    internal inner class ViewHolder {
        var text: TextView? = null
        var imageView: ImageView? = null
        var loadingImage: Image? = null
        var position = 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var resultView = convertView
        if (convertView == null) {
            val inflater = context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val rowView = inflater.inflate(R.layout.feed_cell, parent, false)
            val textView = rowView.findViewById<View>(R.id.topLine) as TextView
            val imageView = rowView.findViewById<View>(R.id.icon) as ImageView
            val holder = ViewHolder()
            holder.text = textView
            holder.imageView = imageView
            resultView = rowView
            resultView.tag = holder
        }

        resultView as View

        val holder = resultView.tag as ViewHolder
        val feed = values[position]
        holder.text!!.text = feed.name
        holder.position = position
        if (feed.image != null) {
            loadImage(feed.image, holder)
        }
        return resultView
    }

    internal fun loadImage(image: Image?, holder: ViewHolder) {
        holder.imageView!!.setImageBitmap(null)
        holder.loadingImage = image
        if (image == null) {
            return
        }
        val position = holder.position
        val task = loadImage(taskManager.threadRunner, image, Integer.toString(holder.hashCode()), object : LoadCallback {
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

    fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {}
    interface FeedsAdapterListener {
        fun getViewAtPosition(position: Int): View?
    }

    init {
        loadingImages = SparseArray()
    }
}