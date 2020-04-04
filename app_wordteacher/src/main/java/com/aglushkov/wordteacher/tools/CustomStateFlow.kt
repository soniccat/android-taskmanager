package com.aglushkov.wordteacher.tools

import com.aglushkov.wordteacher.model.Resource
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
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

fun <T> CustomStateFlow<Resource<T>>?.isUninitialized(): Boolean {
    return this?.value?.isUninitialized() ?: true
}