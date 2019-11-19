package com.rssclient.controllers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.aglushkov.taskmanager_http.image.Image
import com.rssclient.model.RssItem

class RssItemsAdapter(context: Context, val values: ArrayList<RssItem>) : ArrayAdapter<RssItem>(context, R.layout.feed_cell, values) {
    var listener: RssItemsAdapterListener? = null

    internal inner class ViewHolder {
        lateinit var text: TextView
        lateinit var imageView: ImageView
        var loadingImage: Image? = null
        lateinit var progressBar: ProgressBar
        var position = 0
    }

    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val rowView = inflater.inflate(R.layout.feed_cell, parent, false)
            val textView = rowView.findViewById<View>(R.id.topLine) as TextView
            val imageView = rowView.findViewById<View>(R.id.icon) as ImageView

            val holder = ViewHolder()
            holder.text = textView
            holder.imageView = imageView
            holder.progressBar = rowView.findViewById<View>(R.id.progress) as ProgressBar
            holder.progressBar.isIndeterminate = false
            convertView = rowView
            convertView.tag = holder
        }
        val holder = convertView.tag as ViewHolder
        val item = values[position]
        holder.text.text = position.toString() + ". " + item.title
        holder.position = position
        if (item.image != null) {
            holder.progressBar.visibility = View.VISIBLE
            loadImage(convertView, item)
        } else {
            holder.progressBar.visibility = View.INVISIBLE
            holder.imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_launcher))
        }
        return convertView
    }

    protected fun loadImage(convertView: View, item: RssItem) {
        val holder = convertView.tag as ViewHolder
        holder.imageView.setImageDrawable(null)
        holder.loadingImage = item.image
        if (item.image == null) {
            return
        }
        val position = holder.position
        holder.progressBar.progress = 0
        listener?.loadImage(item)
    }

    interface RssItemsAdapterListener {
        fun loadImage(item: RssItem)
    }

}