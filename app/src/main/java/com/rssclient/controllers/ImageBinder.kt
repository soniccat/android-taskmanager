package com.rssclient.controllers

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.aglushkov.taskmanager_http.image.Image
import java.lang.ref.WeakReference

class ImageBinder(private val imageLoader: ImageLoader) {
    fun bind(imageView: ImageView?, image: Image?, placeholder: Drawable? = null, params: Map<String, Any>? = null) {
        if (imageView == null) return

        val imageViewRef = WeakReference(imageView)
        bind(imageView, image, placeholder, params) { bitmap, _ ->
            if (bitmap != null) {
                imageViewRef.get()?.setImageBitmap(bitmap)
            }
        }
    }

    fun bind(imageView: ImageView?,
             image: Image?,
             placeholder: Drawable? = null,
             params: Map<String, Any>? = null,
             completion: (Bitmap?, Exception?) -> Unit) {
        if (imageView == null) return
        if (image != null) {
            val imageHash: Int = image.hashCode()
            if (imageHash != imageView.tag) {
                imageView.tag = imageHash
                imageView.setImageDrawable(placeholder)

                imageLoader.loadImage(image, params) { bitmap: Bitmap?, error: Exception? ->
                    val h: Int = image.hashCode()
                    if (h == imageView.tag) {
                        completion(bitmap, error)
                    }
                }
            }
        } else {
            imageView.tag = null
            imageView.setImageDrawable(placeholder)
            completion(null, null)
        }
    }

    fun unbind(imageView: ImageView?) {
        imageView?.tag = null
        imageView?.setImageDrawable(null)
    }

    interface ImageLoader {
        fun loadImage(image: Image,
                      params: Map<String, Any>?,
                      completion: (bitmap: Bitmap?, error: Exception?) -> Unit)
    }
}