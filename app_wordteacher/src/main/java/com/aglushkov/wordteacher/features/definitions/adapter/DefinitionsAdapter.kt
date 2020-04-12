package com.aglushkov.wordteacher.features.definitions.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aglushkov.modelcore_ui.view.BaseViewItem
import com.aglushkov.wordteacher.features.definitions.vm.WordTitleViewItem
import com.aglushkov.wordteacher.features.definitions.vm.WordTranscriptionViewItem
import com.aglushkov.wordteacher.features.definitions.vm.WordViewItem

class DefinitionsAdapter(val binder: DefinitionsBinder): ListAdapter<BaseViewItem<*>, DefinitionsAdapter.ViewHolder>(BaseViewItem.DiffCallback) {

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = when (viewType) {
            WordTitleViewItem.Type -> createTitleView(parent)
            WordTranscriptionViewItem.Type -> createTranscriptionView(parent)
            else -> throw IllegalArgumentException("Unexpected viewType: $viewType")
        }

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is WordTitleViewItem -> {
                val view = holder.itemView as TextView
                binder.bindTitle(view, item.firstItem())
            }
            is WordTranscriptionViewItem -> {
                val view = holder.itemView as TextView
                binder.bindTranscription(view, item.firstItem())
            }
        }
    }

    private fun createTitleView(parent: ViewGroup): View {
        return createTextView(parent)
    }

    private fun createTranscriptionView(parent: ViewGroup): View {
        return createTextView(parent)
    }

    private fun createTextView(parent: ViewGroup): TextView {
        val textView = TextView(parent.context)
        return textView
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
    }
}