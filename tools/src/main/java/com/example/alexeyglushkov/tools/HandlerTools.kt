package com.example.alexeyglushkov.tools

import android.os.Handler
import android.os.Looper

/**
 * Created by alexeyglushkov on 26.08.16.
 */
object HandlerTools {
    fun runOnHandlerThread(handler: Handler, action: () -> Unit) {
        if (Looper.myLooper() == handler.looper) {
            action()
        } else {
            handler.post { action() }
        }
    }

    fun runOnMainThread(action: () -> Unit) {
        runOnMainThreadDelayed(action, 0)
    }

    fun runOnMainThreadDelayed(action: () -> Unit, delay: Long) {
        val mainLooper = Looper.getMainLooper()
        val hd = Handler(mainLooper)
        hd.postDelayed(action, delay)
    }
}