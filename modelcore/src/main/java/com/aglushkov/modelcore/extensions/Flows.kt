package com.aglushkov.modelcore.extensions

import com.aglushkov.modelcore.resource.CustomStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect


suspend fun <T> Flow<T>.forward(stateFlow: CustomStateFlow<T>) {
    collect {
        stateFlow.channel.offer(it)
    }
}
