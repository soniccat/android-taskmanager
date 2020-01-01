package com.rssclient.rssfeeditems.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rssclient.controllers.R
import com.rssclient.vm.RssView
import kotlinx.android.synthetic.main.feed_cell.view.*

class RssItemsAdapter(private val itemBinder: RssItemBinder)
    : ListAdapter<RssView<*>, RecyclerView.ViewHolder>(RssView.DiffCallback) {

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            RssView.RssItemView.Type -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.feed_cell, parent, false)
                RssItemsViewHolder(view)
            }
            else -> throw IllegalArgumentException("FeedsAdapter.onCreateViewHolder: viewType is ${viewType}")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = getItem(position)
        when (data) {
            is RssView.RssItemView -> {
                holder as RssItemsViewHolder

                val feed = data.firstItem()
                itemBinder.bind(feed, holder)
            }
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is RssItemsViewHolder) {
            itemBinder.clear(holder)
        }
    }

    class RssItemsViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var name = view.name
        var image: ImageView = view.icon
        var progressBar = view.progress

        var progressTag: Int = 0
    }
}