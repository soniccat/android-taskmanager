package com.aglushkov.wordteacher.features.definitions.repository

import com.aglushkov.modelcore.resource.*
import com.aglushkov.wordteacher.model.WordTeacherWord
import com.aglushkov.wordteacher.repository.ServiceRepository
import com.aglushkov.wordteacher.service.WordTeacherWordService
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception

class WordRepository(val serviceRepository: ServiceRepository) {
    val scope = serviceRepository.scope
    val stateFlows: MutableMap<String, CustomStateFlow<Resource<List<WordTeacherWord>>>> = hashMapOf()

    init {
        scope.launch {
            serviceRepository.flow.collect {
                if (it.isLoaded()) {
                    defineUninitializedFlows()
                    // TODO: consider to handle adding new services
                } else if (it is Resource.Error) {
                    setNotLoadedFlowsToError(it.throwable)
                }
            }
        }
    }

    private fun defineUninitializedFlows() {
        for (flowEntry in stateFlows) {
            if (flowEntry.value.isUninitialized()) {
                scope.launch {
                    define(flowEntry.key)
                }
            }
        }
    }

    private fun setNotLoadedFlowsToError(throwable: Throwable) {
        for (flowEntry in stateFlows) {
            if (flowEntry.value.isUninitialized() || flowEntry.value.isLoading()) {
                flowEntry.value.offer(flowEntry.value.value.toError(throwable, true))
            }
        }
    }

    suspend fun define(word: String): CustomStateFlow<Resource<List<WordTeacherWord>>> {
        val services = serviceRepository.services?.data()
        val stateFlow = obtainStateFlow(word)

        if (services != null && services.isNotEmpty() && stateFlow.value.isNotLoadedAndNotLoading()) {
            loadDefinitions(word, services, stateFlow)
        }

        return stateFlow
    }

    fun obtainStateFlow(word: String): CustomStateFlow<Resource<List<WordTeacherWord>>> {
        var stateFlow = stateFlows[word]
        if (stateFlow == null) {
            stateFlow = CustomStateFlow(Resource.Uninitialized())
            stateFlows[word] = stateFlow
        }

        return stateFlow
    }

    private suspend fun loadDefinitions(word: String,
                                        services: List<WordTeacherWordService>,
                                        stateFlow: CustomStateFlow<Resource<List<WordTeacherWord>>>) {
        stateFlow.offer(stateFlow.value.toLoading())

        try {
            val words = mutableListOf<WordTeacherWord>()
            val asyncs = mutableListOf<Deferred<List<WordTeacherWord>>>()

            for (service in services) {
                asyncs.add(scope.async {
                    service.define(word)
                })
            }

            asyncs.forEach {
                try {
                    words.addAll(it.await())
                } catch (e: Exception) {
                }
            }

            // TODO: sort somehow
            stateFlow.offer(stateFlow.value.toLoaded(words.toList()))
        } catch (e: Exception) {
            stateFlow.offer(stateFlow.value.toError(e, true))
        }
    }
}