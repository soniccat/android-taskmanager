package com.aglushkov.wordteacher.features.definitions.vm

import com.aglushkov.general.view.BaseViewItem
import com.aglushkov.wordteacher.model.WordTeacherWord

class WordViewItem(word: WordTeacherWord): BaseViewItem<WordTeacherWord>(word, 10) {
    override fun equalsByIds(item: BaseViewItem<*>): Boolean {
        return item is WordViewItem &&
                type == item.type &&
                firstItem().word == item.firstItem().word
    }
}