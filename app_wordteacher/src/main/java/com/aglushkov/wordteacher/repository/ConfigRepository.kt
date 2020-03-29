package com.aglushkov.wordteacher.repository

import com.aglushkov.wordteacher.model.Resource
import com.aglushkov.wordteacher.model.isNotLoadedAndNotLoading
import com.aglushkov.wordteacher.service.ConfigService
import com.aglushkov.wordteacher.service.decodeConfigs
import com.aglushkov.wordteacher.tools.CustomStateFlow
import com.aglushkov.wordteacher.tools.forward
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception

class ConfigRepository(val service: ConfigService,
                       val scope: CoroutineScope) {
    private val stateFlow = CustomStateFlow<Resource<List<Config>>>(Resource.Uninitialized())
    val flow = stateFlow.flow
    val value: Resource<List<Config>>?
        get() {
            return stateFlow.value
        }

    init {
        loadIfNeeded()
    }

    fun loadIfNeeded() {
        if (value.isNotLoadedAndNotLoading()) {
            load()
        }
    }

    private fun load() {
        scope.launch {
            loadConfigFlow().forward(stateFlow)
        }
    }

    private fun loadConfigFlow() = flow {
        emit(stateFlow.value.toLoading())

        try {
            val body = service.config()
            val configs = ConfigService.decodeConfigs(body)
            emit(Resource.Loaded(configs))
        } catch (e: Exception) {
            emit(stateFlow.value.toError(e, true))
        }
    }

    fun clear() {
        stateFlow.cancel()
    }
}
