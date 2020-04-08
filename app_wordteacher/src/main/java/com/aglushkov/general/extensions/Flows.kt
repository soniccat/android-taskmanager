package com.aglushkov.general.extensions

import com.aglushkov.general.resource.CustomStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect


suspend fun <T> Flow<T>.forward(stateFlow: CustomStateFlow<T>) {
    collect {
        stateFlow.channel.offer(it)
    }
}
