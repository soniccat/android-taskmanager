package com.aglushkov.wordteacher.repository

import android.content.Context
import com.aglushkov.wordteacher.general.resource.Resource
import com.aglushkov.wordteacher.general.resource.isNotLoadedAndNotLoading
import com.aglushkov.wordteacher.general.resource.CustomStateFlow
import com.aglushkov.wordteacher.general.extensions.forward
import com.aglushkov.wordteacher.general.extensions.safeClose
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception

class ConfigConnectParamsStatRepository(val context: Context) {
    private val fileName = "ConfigConnectParamsStats"

    private val stateFlow = CustomStateFlow<Resource<List<ConfigConnectParamsStat>>>(Resource.Uninitialized())
    val flow = stateFlow.flow
    val value: Resource<List<ConfigConnectParamsStat>>?
        get() {
            return stateFlow.value
        }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

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
            loadConfigConnectParamsStatFlow().forward(stateFlow)
        }
    }

    fun addStat(stat: ConfigConnectParamsStat) {
        val list: MutableList<ConfigConnectParamsStat> = stateFlow.value?.data()?.toMutableList() ?: mutableListOf()
        list.add(stat)
        stateFlow.offer(Resource.Loaded(list))
        saveConfigConnectParamsStat()
    }

    private fun loadConfigConnectParamsStatFlow() = flow {
        emit(stateFlow.value.toLoading())

        var stream: InputStream? = null
        try {
            withContext(Dispatchers.IO) {
                stream = context.openFileInput(fileName)
            }
            val configs = ConfigConnectParamsStat.loadFromStream(stream!!)
            emit(Resource.Loaded(configs))
        } catch (e: Exception) {
            emit(Resource.Loaded(emptyList()))
        } finally {
            stream?.safeClose()
        }
    }

    private fun saveConfigConnectParamsStat() = scope.launch {
        val value = stateFlow.value.data() ?: return@launch
        var stream: OutputStream? = null
        try {
            withContext(Dispatchers.IO) {
                stream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            }
            value.saveToStream(stream!!)
        } catch (e: Exception) {
            //...
        } finally {
            stream?.safeClose()
        }
    }

    fun clear() {
        stateFlow.cancel()
    }
}