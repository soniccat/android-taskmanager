package com.aglushkov.modelcore.extensions

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

fun Context.getDrawableCompat(@DrawableRes res: Int): Drawable? = ContextCompat.getDrawable(this, res)

fun Context.getColorCompat(@ColorRes res: Int): Int = ContextCompat.getColor(this, res)

fun Context.getColorStateListCompat(@ColorRes res: Int): ColorStateList? = ContextCompat.getColorStateList(this, res)

fun Context.getLayoutInflater(): LayoutInflater = LayoutInflater.from(this)
