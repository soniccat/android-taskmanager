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
                type == item.type &&
                firstItem() == item.firstItem()
    }
}

class WordTranscriptionViewItem(transcription: String): BaseViewItem<String>(transcription, Type) {
    companion object {
        const val Type = 102
    }

    override fun equalsByIds(item: BaseViewItem<*>): Boolean {
        return item is WordTranscriptionViewItem &&
                type == item.type &&
                firstItem() == item.firstItem()
    }
}