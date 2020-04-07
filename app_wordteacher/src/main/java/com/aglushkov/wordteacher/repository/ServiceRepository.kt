package com.aglushkov.wordteacher.repository

import com.aglushkov.wordteacher.general.resource.Resource
import com.aglushkov.wordteacher.general.resource.merge
import com.aglushkov.wordteacher.service.WordTeacherWordService
import com.aglushkov.wordteacher.general.resource.CustomStateFlow
import com.aglushkov.wordteacher.general.extensions.forward
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ServiceRepository(val configRepository: ConfigRepository,
                        val connectParamsStatRepository: ConfigConnectParamsStatRepository,
                        val serviceFactory: WordTeacherWordServiceFactory) {

    val scope = configRepository.scope
    private val stateFlow = CustomStateFlow<Resource<List<WordTeacherWordService>>>(Resource.Uninitialized())
    val flow = stateFlow.flow

    val services: Resource<List<WordTeacherWordService>>?
        get() {
            return stateFlow.value
        }

    init {
        scope.launch {
            configRepository.flow
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
                        }
                    }

                    it.copyWith(services.toList())
                }.forward(stateFlow)
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