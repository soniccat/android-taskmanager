package com.aglushkov.wordteacher.repository

data class ServiceConfig(
        val keys: List<String>,
        // methodName: (parameterName, parameterValue)
        val methodOptions: ServiceMethodParams)

inline class ServiceMethodParams(val value: Map<String, Map<String, String>>)
