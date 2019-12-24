package com.rssclient.controllers

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.aglushkov.taskmanager_http.image.Image

class ImageBinder(val imageLoader: ImageLoader) {
    fun bind(imageView: ImageView, placeholder: Drawable?, image: Image?, params: Map<String, Any>?) {
        bind(imageView, placeholder, image, params, null)
    }

    fun bind(imageView: ImageView, placeholder: Drawable?, image: Image?, params: Map<String, Any>?, completion: Runnable?) {
        if (image != null) {
            val imageHash: Int = image.hashCode()
            if (imageHash != imageView.getTag()) {
                imageView.setTag(imageHash)
                imageView.setImageDrawable(placeholder)

                imageLoader.loadImage(image, params, { bitmap: Bitmap?, error: Exception? ->
                    val h: Int = image.hashCode()
                    if (h == imageView.getTag()) {
                        if (bitmap != null) {
                            imageView.setImageBitmap(bitmap)
                            completion?.run()
                        }
                    }
                })
            }
        } else {
            imageView.setTag(null)
            imageView.setImageDrawable(placeholder)
            completion?.run()
        }
    }

    interface ImageLoader {
        fun loadImage(image: Image?,
                      params: Map<String, Any>?,
                      completion: (bitmap: Bitmap?, error: java.lang.Exception) -> Unit)
    }
}