package com.aglushkov.modelcore.resource

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

fun <T> CustomStateFlow<Resource<T>>?.isUninitialized(): Boolean {
    return this?.value?.isUninitialized() ?: true
}

fun <T> CustomStateFlow<Resource<T>>?.isLoading(): Boolean {
    return this?.value?.isLoading() ?: false
}