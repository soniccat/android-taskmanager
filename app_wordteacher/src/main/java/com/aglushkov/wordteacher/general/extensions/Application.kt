package com.aglushkov.wordteacher.general.extensions

import android.app.Application
import androidx.annotation.StringRes

fun Application.getResString(@StringRes id: Int): String = applicationContext.resources.getString(id)