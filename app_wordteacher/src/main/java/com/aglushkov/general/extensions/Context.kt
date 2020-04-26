package com.aglushkov.general.extensions

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import com.aglushkov.modelcore.extensions.getDrawableCompat

private val SharedTypedValue = TypedValue()

@ColorInt
fun Context.resolveThemeColor(@AttrRes attribute: Int) = SharedTypedValue.let {
    theme.resolveAttribute(attribute, it, true)
    it.data
}

fun Context.resolveThemeDrawable(@AttrRes attribute: Int) = SharedTypedValue.let {
    theme.resolveAttribute(attribute, it, true)
    getDrawableCompat(it.resourceId)
}

fun Context.resolveThemeStyle(@AttrRes attribute: Int) = SharedTypedValue.let {
    theme.resolveAttribute(attribute, it, true)
    it.resourceId
}