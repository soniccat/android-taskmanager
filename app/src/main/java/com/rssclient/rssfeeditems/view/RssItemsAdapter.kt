package com.rssclient.rssfeeditems.view

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.get
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.alexeyglushkov.ktx.getLayoutInflater
import com.rssclient.controllers.R
import com.rssclient.controllers.databinding.FeedCellBinding
import com.rssclient.vm.RssViewItem
import kotlinx.android.synthetic.main.feed_cell.view.*

class RssItemsAdapter(private val itemBinder: RssItemBinder)
    : ListAdapter<RssViewItem<*>, RecyclerView.ViewHolder>(RssViewItem.DiffCallback) {

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            RssViewItem.RssItemViewItem.Type -> {
                val binding = FeedCellBinding.inflate(parent.getLayoutInflater(), parent, false)
                RssItemsViewHolder(binding)
            }
            else -> throw IllegalArgumentException("FeedsAdapter.onCreateViewHolder: viewType is ${viewType}")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = getItem(position)
        when (data) {
            is RssViewItem.RssItemViewItem -> {
                holder as RssItemsViewHolder
                holder.bindPosition = position

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

    class RssItemsViewHolder(binding: FeedCellBinding): RecyclerView.ViewHolder(binding.root) {
        var name = binding.name
        var image = binding.icon
        var progressBar = binding.progress

        var bindPosition: Int = -1
        var progressTag: String = ""

        fun showProgress(progress: Float) {
            progressBar.progress = (progress * 100.0f).toInt()
        }

        fun showImage(anImg: Drawable?) {
            image.setImageDrawable(anImg)
            image.visibility = View.VISIBLE
            progressBar.visibility = View.INVISIBLE
        }
    }
}