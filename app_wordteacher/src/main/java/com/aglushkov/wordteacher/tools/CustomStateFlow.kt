package com.aglushkov.wordteacher.tools

import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow

class CustomStateFlow<T>(v: T) {
    val channel =  ConflatedBroadcastChannel<T>(v)
    val value: T
        get() {
            return channel.value!!
        }
    val flow = channel.asFlow()

    fun offer(v: T) = channel.offer(v)

    fun cancel() = channel.cancel()
}