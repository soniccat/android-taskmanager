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
}