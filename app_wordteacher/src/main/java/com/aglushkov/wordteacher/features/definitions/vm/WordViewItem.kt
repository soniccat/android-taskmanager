package com.aglushkov.wordteacher.features.definitions.vm

import com.aglushkov.modelcore_ui.view.BaseViewItem
import com.aglushkov.wordteacher.model.WordTeacherWord

class WordViewItem(word: WordTeacherWord): BaseViewItem<WordTeacherWord>(word, Type) {
    companion object {
        const val Type = 100
    }

    override fun equalsByIds(item: BaseViewItem<*>): Boolean {
        return item is WordViewItem &&
                type == item.type &&
                firstItem().word == item.firstItem().word
    }
}

class WordTitleViewItem(title: String): BaseViewItem<String>(title, Type) {
    companion object {
        const val Type = 101
    }

    override fun equalsByIds(item: BaseViewItem<*>): Boolean {
        return item is WordTitleViewItem &&
                type == item.type
    }
}

class WordTranscriptionViewItem(transcription: String): BaseViewItem<String>(transcription, Type) {
    companion object {
        const val Type = 102
    }

    override fun equalsByIds(item: BaseViewItem<*>): Boolean {
        return item is WordTranscriptionViewItem &&
                type == item.type
    }
}

class WordPartOfSpeechViewItem(partOfSpeech: String): BaseViewItem<String>(partOfSpeech, Type) {
    companion object {
        const val Type = 103
    }

    override fun equalsByIds(item: BaseViewItem<*>): Boolean {
        return item is WordPartOfSpeechViewItem &&
                type == item.type
    }
}

class WordDefinitionViewItem(definition: String): BaseViewItem<String>(definition, Type) {
    companion object {
        const val Type = 104
    }

    override fun equalsByIds(item: BaseViewItem<*>): Boolean {
        return item is WordDefinitionViewItem &&
                type == item.type
    }
}

class WordExampleViewItem(example: String): BaseViewItem<String>(example, Type) {
    companion object {
        const val Type = 105
    }

    override fun equalsByIds(item: BaseViewItem<*>): Boolean {
        return item is WordExampleViewItem &&
                type == item.type
    }
}

class WordSynonymViewItem(synonym: String): BaseViewItem<String>(synonym, Type) {
    companion object {
        const val Type = 106
    }

    override fun equalsByIds(item: BaseViewItem<*>): Boolean {
        return item is WordSynonymViewItem &&
                type == item.type
    }
}

class WordSubHeaderViewItem(name: String): BaseViewItem<String>(name, Type) {
    companion object {
        const val Type = 107
    }

    override fun equalsByIds(item: BaseViewItem<*>): Boolean {
        return item is WordSubHeaderViewItem &&
                type == item.type
    }
}

class WordDividerViewItem(): BaseViewItem<Any>(Obj, Type) {
    companion object {
        final val Obj = Object()
        const val Type = 108
    }

    override fun equalsByIds(item: BaseViewItem<*>): Boolean {
        return item is WordDividerViewItem &&
                type == item.type
    }
}