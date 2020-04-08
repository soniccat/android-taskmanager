package com.aglushkov.general.extensions

import java.io.Closeable

fun Closeable.safeClose() {
    try {
        close()
    } catch (e: Exception) {
    }
}