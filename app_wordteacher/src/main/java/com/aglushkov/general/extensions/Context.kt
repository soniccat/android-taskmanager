package com.aglushkov.general.extensions

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import com.aglushkov.modelcore.extensions.getColorCompat
import com.aglushkov.modelcore.extensions.getDrawableCompat

@ColorInt
fun Context.resolveThemeColor(@AttrRes attribute: Int) = TypedValue().let {
    theme.resolveAttribute(attribute, it, true);
    it.data
}

fun Context.resolveThemeDrawable(@AttrRes attribute: Int) = TypedValue().let {
    theme.resolveAttribute(attribute, it, true);
    getDrawableCompat(it.resourceId)
}

fun Context.resolveThemeStyle(@AttrRes attribute: Int) = TypedValue().let {
    theme.resolveAttribute(attribute, it, true);
    it.resourceId
}