package com.aglushkov.wordteacher.repository

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.aglushkov.wordteacher.model.Resource
import com.aglushkov.wordteacher.model.isNotLoadedAndNotLoading
import com.aglushkov.wordteacher.service.decodeConfigs
import com.aglushkov.wordteacher.tools.forward
import com.aglushkov.wordteacher.tools.safeClose
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception

class ConfigConnectParamsStatRepository(val context: Context) {
    private val fileName = "ConfigConnectParamsStats"
    private val channel =  ConflatedBroadcastChannel<Resource<List<ConfigConnectParamsStat>>>(Resource.Uninitialized())
    val flow = channel.asFlow()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

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
            loadConfigConnectParamsStatFlow().forward(channel)
        }
    }

    fun addStat(stat: ConfigConnectParamsStat) {
        val list: MutableList<ConfigConnectParamsStat> = channel.value?.data()?.toMutableList() ?: mutableListOf()
        list.add(stat)
        channel.offer(Resource.Loaded(list))
        saveConfigConnectParamsStat()
    }

    private fun loadConfigConnectParamsStatFlow() = flow {
        emit(channel.value.toLoading())

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
        val value = channel.value.data() ?: return@launch
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
        channel.cancel()
    }
}