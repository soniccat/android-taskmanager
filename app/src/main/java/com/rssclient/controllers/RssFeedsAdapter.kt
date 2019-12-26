package com.rssclient.controllers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.alexeyglushkov.ext.getDrawableCompat
import com.rssclient.vm.RssItemView
import kotlinx.android.synthetic.main.feed_cell.view.*

class RssFeedsAdapter(private val imageBinder: ImageBinder)
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

                val placeholder = holder.itemView.context.getDrawableCompat(R.drawable.ic_launcher)
                imageBinder.bind(holder.image!!, item.image, placeholder)
            }
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is RssFeedViewHolder) {
            imageBinder.unbind(holder.image)
        }
    }

    class RssFeedViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var name: TextView? = null
        var image: ImageView? = null

        init {
            name = view.name // TODO: switch to Google bindings instead of synthetic
            image = view.icon
        }
    }
}