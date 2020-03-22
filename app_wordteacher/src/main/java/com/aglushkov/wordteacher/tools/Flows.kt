package com.aglushkov.wordteacher.tools

import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect


suspend fun <T> Flow<T>.forward(channel: SendChannel<T>) {
    collect {
        channel.offer(it)
    }
}