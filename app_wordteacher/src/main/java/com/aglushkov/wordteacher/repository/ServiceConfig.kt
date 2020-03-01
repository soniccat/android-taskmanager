package com.aglushkov.wordteacher.repository

data class ServiceConfig(
        val baseUrls: List<String>,
        val keys: List<String>,
        // methodName: (parameterName, parameterValue)
        val methodOptions: ServiceMethodParams = ServiceMethodParams(emptyMap()))

inline class ServiceMethodParams(val value: Map<String, Map<String, String>>)
