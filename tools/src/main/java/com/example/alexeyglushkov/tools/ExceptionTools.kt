package com.example.alexeyglushkov.tools

/**
 * Created by alexeyglushkov on 24.02.18.
 */
object ExceptionTools {
    @JvmStatic
    @Throws(NullPointerException::class)
    fun throwIfNull(obj: Any?, message: String?) {
        if (obj == null) {
            throw NullPointerException(message)
        }
    }
}