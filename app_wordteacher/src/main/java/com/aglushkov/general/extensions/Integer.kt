package com.aglushkov.general.extensions

import android.content.Context

fun Int.pxToDp(context: Context) = (this * context.resources.displayMetrics.density).toInt()