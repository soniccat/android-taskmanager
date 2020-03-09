package com.aglushkov.wordteacher.repository

import com.aglushkov.wordteacher.model.Resource
import com.aglushkov.wordteacher.model.isNotLoadedAndNotLoading
import com.aglushkov.wordteacher.service.ConfigService
import com.aglushkov.wordteacher.service.decodeConfigs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.lang.Exception

class ConfigRepository(val service: ConfigService,
                       val scope: CoroutineScope) {
    private val channel =  ConflatedBroadcastChannel<Resource<List<Config>>>(Resource.Uninitialized())
    val flow = channel.asFlow()

    init {
        loadIfNeeded()
    }

    fun loadIfNeeded() {
        if (channel.value.isNotLoadedAndNotLoading()) {
            load()
        }
    }

    private fun load() {
        scope.launch {
            loadFlow().forward(channel)
        }
    }

    private fun loadFlow() = flow<Resource<List<Config>>> {
        emit(channel.value.toLoading())

        try {
            val body = service.config()
            val configs = ConfigService.decodeConfigs(body)
            emit(Resource.Loaded(configs))
        } catch (e: Exception) {
            emit(channel.value.toError(e, true))
        }
    }

    fun clear() {
        channel.cancel()
    }
}

suspend fun <T> Flow<T>.forward(channel: SendChannel<T>) {
    collect {
        channel.offer(it)
    }
}