package com.rssclient.rssfeed.view

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.alexeyglushkov.ktx.getLayoutInflater
import com.rssclient.controllers.databinding.FeedCellBinding
import com.rssclient.vm.RssViewItem

class RssFeedsAdapter(private val feedBinder: RssFeedBinder)
    : ListAdapter<RssViewItem<*>, RecyclerView.ViewHolder>(RssViewItem.DiffCallback) {

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            RssViewItem.RssFeedViewItem.Type -> {
                val binding = FeedCellBinding.inflate(parent.getLayoutInflater(), parent, false)
                RssFeedViewHolder(binding)
            }
            else -> throw IllegalArgumentException("FeedsAdapter.onCreateViewHolder: viewType is $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val data = getItem(position)) {
            is RssViewItem.RssFeedViewItem -> {
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

    class RssFeedViewHolder(binding: FeedCellBinding): RecyclerView.ViewHolder(binding.root) {
        val name = binding.name
        val image = binding.icon
    }
}