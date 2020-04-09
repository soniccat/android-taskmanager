package com.aglushkov.modelcore.vm

open class Event {
    var isHandled: Boolean = false
        private set

    fun take(): Event? {
        return if (isHandled) {
            null
        } else {
            isHandled = true
            this
        }
    }
}