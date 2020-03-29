package com.aglushkov.wordteacher.repository

import android.app.Service
import com.aglushkov.wordteacher.model.Resource
import com.aglushkov.wordteacher.model.merge
import com.aglushkov.wordteacher.service.WordTeacherWordService
import com.aglushkov.wordteacher.tools.collect
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ServiceRepository(val configRepository: ConfigRepository,
                        val connectParamsStatRepository: ConfigConnectParamsStatRepository,
                        val serviceFactory: WordTeacherWordServiceFactory) {

    val scope = configRepository.scope
    private val serviceChannel = ConflatedBroadcastChannel<Resource<List<WordTeacherWordService>>>()
    val flow = serviceChannel.asFlow()
    val services: Resource<List<WordTeacherWordService>>?
        get() {
            return serviceChannel.valueOrNull
        }

    init {
        scope.launch {
            serviceChannel.collect(configRepository.flow
                .combine(connectParamsStatRepository.flow) { a, b ->
                    a.merge(b)
                }.map {
                    val services: MutableList<WordTeacherWordService> = mutableListOf()
                    if (it is Resource.Loaded) {
                        val config = it.data.first
                        val connectParamsStat = it.data.second

                        if (config != null && connectParamsStat != null) {
                            val filteredServices = config.mapNotNull {
                                createWordTeacherWordService(it)
                            }
                            services.addAll(filteredServices)
                            it.copyWith(services.toList())
                        } else {
                            it.copyWith(services.toList())
                        }
                    } else {
                        it.copyWith(services.toList())
                    }
                })
        }
    }

    private fun createWordTeacherWordService(it: Config): WordTeacherWordService? {
        // TODO: filter connectParams with connectParamsStat
        val connectParams = it.connectParams.first()
        return serviceFactory.createService(it.type, connectParams, it.methods)
    }

    init {
    }
}