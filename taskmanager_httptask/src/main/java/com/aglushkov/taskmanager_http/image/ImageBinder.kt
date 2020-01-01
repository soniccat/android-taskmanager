package com.aglushkov.taskmanager_http.image

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import java.lang.ref.WeakReference

class ImageBinder(private val imageLoader: ImageLoader) {
    fun bind(imageView: ImageView?,
             image: Image?,
             placeholder: Drawable? = null,
             params: Map<String, Any> = emptyMap()) : Any? {
        if (imageView == null) return null

        val imageViewRef = WeakReference(imageView)
        return bind(imageView, image, placeholder, params) { bitmap, _ ->
            if (bitmap != null) {
                imageViewRef.get()?.setImageBitmap(bitmap)
            }
        }
    }

    fun bind(imageView: ImageView?,
             image: Image?,
             placeholder: Drawable?,
             params: Map<String, Any>,
             completion: (Bitmap?, Exception?) -> Unit): Any? {
        if (imageView == null) return null
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

        return imageView.tag
    }

    fun clear(imageView: ImageView?) {
        imageView?.tag = null
        imageView?.setImageDrawable(null)
    }

    interface ImageLoader {
        fun loadImage(image: Image,
                      params: Map<String, Any>,
                      completion: (bitmap: Bitmap?, error: Exception?) -> Unit)
    }
}