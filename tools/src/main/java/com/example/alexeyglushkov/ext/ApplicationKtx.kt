package com.example.alexeyglushkov.ext

import android.app.Application
import androidx.annotation.StringRes

fun Application.getResString(@StringRes id: Int) = applicationContext.resources.getString(id)