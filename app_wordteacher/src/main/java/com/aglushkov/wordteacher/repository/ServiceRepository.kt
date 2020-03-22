package com.aglushkov.wordteacher.repository

import com.aglushkov.wordteacher.apiproviders.owlbot.service.OwlBotService
import com.aglushkov.wordteacher.apiproviders.owlbot.service.createWordTeacherWordService
import com.aglushkov.wordteacher.model.Resource
import com.aglushkov.wordteacher.model.isLoaded
import com.aglushkov.wordteacher.model.merge
import com.aglushkov.wordteacher.service.WordTeacherWordService
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.combineLatest
import kotlinx.coroutines.flow.map

class ServiceRepository(val configRepository: ConfigRepository,
                        val connectParamsStatRepository: ConfigConnectParamsStatRepository) {


    val services = configRepository.flow.combine(connectParamsStatRepository.flow, { a, b ->
        a.merge(b)
    }).map {
        val services: MutableList<WordTeacherWordService> = mutableListOf()
        if (it is Resource.Loaded) {
            val config = it.data.first
            val connectParamsStat = it.data.second

            if (config != null && connectParamsStat != null) {
                val filteredServices = config.mapNotNull {
                    when (it.type) {
                        Config.Type.OwlBot -> {
                            // TODO: filter connectParams with connectParamsStat
                            val connectParams = it.connectParams.first()
                            OwlBotService.createWordTeacherWordService(connectParams.baseUrls, connectParams.key)
                        }
                        else -> null
                    }
                }
                services.addAll(filteredServices)
                it.copyWith(services)
            } else {
                it.copyWith(services)
            }
        } else {
            it.copyWith(services)
        }
    }

    init {
    }
}