package com.aglushkov.wordteacher.features.definitions.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aglushkov.general.extensions.resolveThemeDrawable
import com.aglushkov.general.extensions.pxToDp
import com.aglushkov.modelcore_ui.view.BaseViewItem
import com.aglushkov.wordteacher.R
import com.aglushkov.wordteacher.features.definitions.vm.*

class DefinitionsAdapter(val binder: DefinitionsBinder): ListAdapter<BaseViewItem<*>, DefinitionsAdapter.ViewHolder>(BaseViewItem.DiffCallback) {

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val lp = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
        val view = when (viewType) {
            WordTitleViewItem.Type -> createTitleView(parent, lp)
            WordTranscriptionViewItem.Type -> createTranscriptionView(parent, lp)
            WordPartOfSpeechViewItem.Type -> createPartOfSpeechView(parent, lp)
            WordDefinitionViewItem.Type -> createDefinitionView(parent, lp)
            WordExampleViewItem.Type -> createExampleView(parent, lp)
            WordSynonymViewItem.Type -> createSynonymView(parent, lp)
            WordSubHeaderViewItem.Type -> createSubHeaderView(parent, lp)
            WordDividerViewItem.Type -> createDividerView(parent, lp)
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

    private fun createTitleView(parent: ViewGroup, lp: RecyclerView.LayoutParams): View {
        return createTextView(parent).apply {
            layoutParams = lp
        }
    }

    private fun createTranscriptionView(parent: ViewGroup, lp: RecyclerView.LayoutParams): View {
        return createTextView(parent).apply {
            layoutParams = lp
        }
    }

    private fun createPartOfSpeechView(parent: ViewGroup, lp: RecyclerView.LayoutParams): View {
        return createTextView(parent).apply {
            layoutParams = lp
        }
    }

    private fun createDefinitionView(parent: ViewGroup, lp: RecyclerView.LayoutParams): View {
        return createTextView(parent).apply {
            layoutParams = lp
        }
    }

    private fun createExampleView(parent: ViewGroup, lp: RecyclerView.LayoutParams): View {
        return createTextView(parent).apply {
            layoutParams = lp
        }
    }

    private fun createSynonymView(parent: ViewGroup, lp: RecyclerView.LayoutParams): View {
        return createTextView(parent).apply {
            layoutParams = lp
        }
    }

    private fun createSubHeaderView(parent: ViewGroup, lp: RecyclerView.LayoutParams): View {
        return createTextView(parent).apply {
            layoutParams = lp
        }
    }

    private fun createDividerView(parent: ViewGroup, lp: RecyclerView.LayoutParams): View {
        val context = parent.context
        return View(context).apply {
            background = context.resolveThemeDrawable(R.attr.dividerHorizontal)

            lp.height = 1.pxToDp(context)
            lp.topMargin = context.resources.getDimensionPixelOffset(R.dimen.word_divider_top_margin)
            lp.bottomMargin = context.resources.getDimensionPixelOffset(R.dimen.word_divider_bottom_margin)
            layoutParams = lp
        }
    }

    private fun createTextView(parent: ViewGroup): TextView {
        val textView = TextView(parent.context)
        return textView
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
    }
}