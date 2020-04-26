package com.aglushkov.general.extensions

import com.google.android.material.chip.ChipGroup

fun ChipGroup.updateChildClickable() {
    for (i in 0 until childCount) {
        val child = getChildAt(i)
        child.isClickable = child.id != checkedChipId
    }
}