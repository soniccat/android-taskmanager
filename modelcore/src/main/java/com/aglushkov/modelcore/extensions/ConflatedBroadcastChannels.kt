package com.aglushkov.modelcore.extensions

import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

suspend fun <T> ConflatedBroadcastChannel<T>.collect(flow: Flow<T>) {
    flow.collect {
        offer(it)
    }
}