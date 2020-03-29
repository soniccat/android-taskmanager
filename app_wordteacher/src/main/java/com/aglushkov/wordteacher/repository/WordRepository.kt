package com.aglushkov.wordteacher.repository

import com.aglushkov.wordteacher.model.Resource
import com.aglushkov.wordteacher.model.WordTeacherWord
import com.aglushkov.wordteacher.model.isLoaded
import com.aglushkov.wordteacher.service.WordTeacherWordService
import com.aglushkov.wordteacher.tools.CustomStateFlow
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.Exception

class WordRepository(val serviceRepository: ServiceRepository) {
    val scope = serviceRepository.scope
    val stateFlows: MutableMap<String, CustomStateFlow<Resource<List<WordTeacherWord>>>> = hashMapOf()

    init {
        scope.launch {
            serviceRepository.flow.collect {
                if (it.isLoaded()) {
                    // TODO: handle services changes
                }
            }
        }
    }

    suspend fun define(word: String) {
        val services = serviceRepository.services?.data()
        if (services != null && services.isNotEmpty()) {
            val stateFlow = obtainStateFlow(word)
            defineFlow(word, services, stateFlow)
        }
    }

    private fun obtainStateFlow(word: String): CustomStateFlow<Resource<List<WordTeacherWord>>> {
        var stateFlow = stateFlows[word]
        if (stateFlow == null) {
            stateFlow = CustomStateFlow(Resource.Uninitialized())
            stateFlows[word] = stateFlow
        }

        return stateFlow
    }

    suspend fun defineFlow(word: String,
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