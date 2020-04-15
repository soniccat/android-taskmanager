package com.aglushkov.wordteacher.features.definitions.vm

import com.aglushkov.modelcore_ui.view.BaseViewItem

class DefinitionsDisplayModeViewItem(modes: List<DefinitionsDisplayMode>,
                                     internal val selected: DefinitionsDisplayMode): BaseViewItem<DefinitionsDisplayMode>(modes, Type) {
    companion object {
        const val Type = 200
    }
}