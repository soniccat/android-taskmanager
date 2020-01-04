package com.example.alexeyglushkov.ktx

import android.app.Application
import androidx.annotation.StringRes

fun Application.getResString(@StringRes id: Int): String = applicationContext.resources.getString(id)