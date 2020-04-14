package com.aglushkov.wordteacher.features.definitions.vm

import com.aglushkov.modelcore_ui.view.BaseViewItem
import com.aglushkov.wordteacher.model.WordTeacherWord

class WordViewItem(word: WordTeacherWord): BaseViewItem<WordTeacherWord>(word, Type) {
    companion object {
        const val Type = 100
    }
}

class WordTitleViewItem(title: String): BaseViewItem<String>(title, Type) {
    companion object {
        const val Type = 101
    }
}

class WordTranscriptionViewItem(transcription: String): BaseViewItem<String>(transcription, Type) {
    companion object {
        const val Type = 102
    }
}

class WordPartOfSpeechViewItem(partOfSpeech: String): BaseViewItem<String>(partOfSpeech, Type) {
    companion object {
        const val Type = 103
    }
}

class WordDefinitionViewItem(definition: String): BaseViewItem<String>(definition, Type) {
    companion object {
        const val Type = 104
    }
}

class WordExampleViewItem(example: String): BaseViewItem<String>(example, Type) {
    companion object {
        const val Type = 105
    }
}

class WordSynonymViewItem(synonym: String): BaseViewItem<String>(synonym, Type) {
    companion object {
        const val Type = 106
    }
}

class WordSubHeaderViewItem(name: String): BaseViewItem<String>(name, Type) {
    companion object {
        const val Type = 107
    }
}

class WordDividerViewItem(): BaseViewItem<Any>(Obj, Type) {
    companion object {
        val Obj = Object()
        const val Type = 108
    }
}