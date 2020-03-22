package com.aglushkov.wordteacher.repository

import com.aglushkov.wordteacher.model.Resource
import com.aglushkov.wordteacher.model.isNotLoadedAndNotLoading
import com.aglushkov.wordteacher.service.ConfigService
import com.aglushkov.wordteacher.service.decodeConfigs
import com.aglushkov.wordteacher.tools.forward
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
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
            loadConfigFlow().forward(channel)
        }
    }

    private fun loadConfigFlow() = flow<Resource<List<Config>>> {
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
