package com.aglushkov.wordteacher.features.definitions.adapter

import android.widget.TextView
import com.aglushkov.wordteacher.R
import com.aglushkov.wordteacher.features.definitions.view.WordTitleView
import com.aglushkov.wordteacher.features.definitions.vm.DefinitionsDisplayMode
import com.aglushkov.wordteacher.features.definitions.vm.DefinitionsDisplayModeViewItem
import com.google.android.material.chip.ChipGroup

class DefinitionsBinder {
    var listener: Listener? = null

    fun bindDisplayMode(chipGroup: ChipGroup, item: DefinitionsDisplayModeViewItem) {
        val chipId = when (item.selected) {
            DefinitionsDisplayMode.Merged -> R.id.definitions_displayMode_merged
            else -> R.id.definitions_displayMode_bySource
        }
        chipGroup.check(chipId)

        chipGroup.setOnCheckedChangeListener { group, checkedId ->
            val mode = when (checkedId) {
                R.id.definitions_displayMode_merged -> DefinitionsDisplayMode.Merged
                else -> DefinitionsDisplayMode.BySource
            }
            listener?.onDisplayModeChanged(mode)
        }
    }

    fun bindTitle(view: WordTitleView, title: String) {
        view.title.text = title
        view.providedBy.text = "aa"
    }

    fun bindTranscription(view: TextView, transcription: String) {
        view.text = transcription
    }

    fun bindPartOfSpeech(view: TextView, partOfSpeech: String) {
        view.text = partOfSpeech
    }

    fun bindDefinition(view: TextView, definition: String) {
        view.text = definition
    }

    fun bindExample(view: TextView, example: String) {
        view.text = example
    }

    fun bindSynonym(view: TextView, synonym: String) {
        view.text = synonym
    }

    fun bindSubHeader(view: TextView, text: String) {
        view.text = text
    }

    interface Listener {
        fun onDisplayModeChanged(mode: DefinitionsDisplayMode)
    }
}