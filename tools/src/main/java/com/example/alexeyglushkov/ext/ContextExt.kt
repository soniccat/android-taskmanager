package com.example.alexeyglushkov.ext

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

fun Context.getDrawableCompat(@DrawableRes res: Int): Drawable? = ContextCompat.getDrawable(this, res)

fun Context.getLayoutInflater(): LayoutInflater = LayoutInflater.from(this)