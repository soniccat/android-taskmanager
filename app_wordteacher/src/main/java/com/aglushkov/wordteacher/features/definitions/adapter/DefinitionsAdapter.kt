package com.aglushkov.wordteacher.features.definitions.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aglushkov.modelcore_ui.view.BaseViewItem
import com.aglushkov.wordteacher.features.definitions.vm.*

class DefinitionsAdapter(val binder: DefinitionsBinder): ListAdapter<BaseViewItem<*>, DefinitionsAdapter.ViewHolder>(BaseViewItem.DiffCallback) {

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = when (viewType) {
            WordTitleViewItem.Type -> createTitleView(parent)
            WordTranscriptionViewItem.Type -> createTranscriptionView(parent)
            WordPartOfSpeechViewItem.Type -> createPartOfSpeechView(parent)
            WordDefinitionViewItem.Type -> createDefinitionView(parent)
            WordExampleViewItem.Type -> createExampleView(parent)
            WordSynonymViewItem.Type -> createSynonymView(parent)
            WordSubHeaderViewItem.Type -> createSubHeaderView(parent)
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
            is WordPartOfSpeechViewItem -> {
                val view = holder.itemView as TextView
                binder.bindPartOfSpeech(view, item.firstItem())
            }
            is WordDefinitionViewItem -> {
                val view = holder.itemView as TextView
                binder.bindDefinition(view, item.firstItem())
            }
            is WordExampleViewItem -> {
                val view = holder.itemView as TextView
                binder.bindExample(view, item.firstItem())
            }
            is WordSynonymViewItem -> {
                val view = holder.itemView as TextView
                binder.bindSynonym(view, item.firstItem())
            }
            is WordSubHeaderViewItem -> {
                val view = holder.itemView as TextView
                binder.bindSubHeader(view, item.firstItem())
            }
        }
    }

    private fun createTitleView(parent: ViewGroup): View {
        return createTextView(parent)
    }

    private fun createTranscriptionView(parent: ViewGroup): View {
        return createTextView(parent)
    }

    private fun createPartOfSpeechView(parent: ViewGroup): View {
        return createTextView(parent)
    }

    private fun createExampleView(parent: ViewGroup): View {
        return createTextView(parent)
    }

    private fun createSynonymView(parent: ViewGroup): View {
        return createTextView(parent)
    }

    private fun createSubHeaderView(parent: ViewGroup): View {
        return createTextView(parent)
    }

    private fun createDefinitionView(parent: ViewGroup): View {
        return createTextView(parent)
    }

    private fun createTextView(parent: ViewGroup): TextView {
        val textView = TextView(parent.context)
        return textView
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
    }
}