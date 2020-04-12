package com.aglushkov.wordteacher.features.definitions.adapter

import android.view.View
import android.widget.TextView
import com.aglushkov.wordteacher.features.definitions.vm.WordViewItem

class DefinitionsBinder {
    fun bindTitle(view: TextView, title: String) {
        view.text = title
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
}