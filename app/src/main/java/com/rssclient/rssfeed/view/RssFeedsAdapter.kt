package com.rssclient.rssfeed.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rssclient.controllers.R
import com.rssclient.vm.RssView
import kotlinx.android.synthetic.main.feed_cell.view.*

class RssFeedsAdapter(private val feedBinder: RssFeedBinder)
    : ListAdapter<RssView<*>, RecyclerView.ViewHolder>(RssView.DiffCallback) {

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            RssView.RssFeedView.Type -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.feed_cell, parent, false)
                RssFeedViewHolder(view)
            }
            else -> throw IllegalArgumentException("FeedsAdapter.onCreateViewHolder: viewType is ${viewType}")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = getItem(position)
        when (data) {
            is RssView.RssFeedView -> {
                holder as RssFeedViewHolder

                val feed = data.firstItem()
                feedBinder.bind(feed, holder)
            }
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is RssFeedViewHolder) {
            feedBinder.clear(holder)
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