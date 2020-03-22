package com.aglushkov.wordteacher.tools

import java.io.Closeable

fun Closeable.safeClose() {
    try {
        close()
    } catch (e: Exception) {
    }
}