package com.aglushkov.wordteacher.features.definitions.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aglushkov.general.extensions.resolveThemeDrawable
import com.aglushkov.general.extensions.pxToDp
import com.aglushkov.general.extensions.resolveThemeStyle
import com.aglushkov.modelcore_ui.view.BaseViewItem
import com.aglushkov.wordteacher.R
import com.aglushkov.wordteacher.features.definitions.vm.*
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class DefinitionsAdapter(val binder: DefinitionsBinder): ListAdapter<BaseViewItem<*>, DefinitionsAdapter.ViewHolder>(BaseViewItem.DiffCallback) {

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val lp = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
        val view = when (viewType) {
            DefinitionsDisplayModeViewItem.Type -> createWordDisplayView(parent, lp)
            // Word Card Items
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
            is DefinitionsDisplayModeViewItem -> {
                val view = holder.itemView as ChipGroup
                binder.bindDisplayMode(view, item.items)
            }
            // Word Card Items
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

    private fun createWordDisplayView(parent: ViewGroup, lp: RecyclerView.LayoutParams): View {
        val context = parent.context
        val group = ChipGroup(context)

        val byCardChip = createChip(context,
                R.id.definitions_displayMode_bySource,
                context.getString(R.string.definitions_displayMode_bySource))
        val mergedChip = createChip(context,
                R.id.definitions_displayMode_merged,
                context.getString(R.string.definitions_displayMode_merge))

        val padding = context.resources.getDimensionPixelSize(R.dimen.definitions_displayMode_padding)
        group.updatePadding(left = padding, top = padding, right = padding)
        group.addView(byCardChip)
        group.addView(mergedChip)

        group.isSingleSelection = true
        group.layoutParams = lp
        return group
    }

    private fun createChip(context: Context, id: Int, text: String): Chip {
        val chip = Chip(context)
        chip.id = id
        chip.text = text
        chip.isCheckable = true
        chip.isCheckedIconVisible = false

        return chip
    }

    // Word View Item

    private fun createTitleView(parent: ViewGroup, lp: RecyclerView.LayoutParams): View {
        return createTextView(parent).apply {
            setTextAppearance(parent.context, parent.context.resolveThemeStyle(R.attr.wordTitleTextAppearance))
            layoutParams = lp
        }
    }

    private fun createTranscriptionView(parent: ViewGroup, lp: RecyclerView.LayoutParams): View {
        return createTextView(parent).apply {
            setTextAppearance(parent.context, parent.context.resolveThemeStyle(R.attr.wordTranscriptionTextAppearance))
            layoutParams = lp
        }
    }

    private fun createPartOfSpeechView(parent: ViewGroup, lp: RecyclerView.LayoutParams): View {
        return createTextView(parent).apply {
            setTextAppearance(context, parent.context.resolveThemeStyle(R.attr.wordPartOfSpeechTextAppearance))
            lp.topMargin = context.resources.getDimensionPixelOffset(R.dimen.word_partOfSpeech_topMargin)
            layoutParams = lp
        }
    }

    private fun createDefinitionView(parent: ViewGroup, lp: RecyclerView.LayoutParams): View {
        return createTextView(parent).apply {
            setTextAppearance(parent.context, parent.context.resolveThemeStyle(R.attr.wordDefinitionTextAppearance))
            layoutParams = lp
        }
    }

    private fun createExampleView(parent: ViewGroup, lp: RecyclerView.LayoutParams): View {
        return createTextView(parent).apply {
            setTextAppearance(parent.context, parent.context.resolveThemeStyle(R.attr.wordDefinitionTextAppearance))
            layoutParams = lp
        }
    }

    private fun createSynonymView(parent: ViewGroup, lp: RecyclerView.LayoutParams): View {
        return createTextView(parent).apply {
            setTextAppearance(parent.context, parent.context.resolveThemeStyle(R.attr.wordDefinitionTextAppearance))
            layoutParams = lp
        }
    }

    private fun createSubHeaderView(parent: ViewGroup, lp: RecyclerView.LayoutParams): View {
        return createTextView(parent).apply {
            setTextAppearance(parent.context, parent.context.resolveThemeStyle(R.attr.wordSubHeaderTextAppearance))
            lp.topMargin = context.resources.getDimensionPixelOffset(R.dimen.word_subheader_topMargin)
            layoutParams = lp
        }
    }

    private fun createDividerView(parent: ViewGroup, lp: RecyclerView.LayoutParams): View {
        val context = parent.context
        val view = View(context)
        view.background = context.resolveThemeDrawable(R.attr.dividerHorizontal)
        setWordHorizontalPadding(view)

        lp.height = 1.pxToDp(context)
        lp.topMargin = context.resources.getDimensionPixelOffset(R.dimen.word_divider_topMargin)
        lp.bottomMargin = context.resources.getDimensionPixelOffset(R.dimen.word_divider_bottomMargin)
        view.layoutParams = lp

        return view
    }

    private fun createTextView(parent: ViewGroup): TextView {
        val textView = TextView(parent.context)
        setWordHorizontalPadding(textView)
        return textView
    }

    private fun setWordHorizontalPadding(view: View) {
        val horizontalPadding = view.resources.getDimensionPixelOffset(R.dimen.word_horizontalPadding)
        view.updatePadding(left = horizontalPadding, right = horizontalPadding)
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
    }
}