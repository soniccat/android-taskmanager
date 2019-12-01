package com.example.alexeyglushkov.authorization.requestbuilder

import org.junit.Assert
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author Pablo Fernandez
 */
class ParameterList {
    private val params: MutableList<Parameter>

    constructor() {
        params = ArrayList()
    }

    internal constructor(params: List<Parameter>?) {
        this.params = ArrayList(params)
    }

    constructor(map: Map<String, String>) : this() {
        for ((key, value) in map) {
            params.add(Parameter(key, value))
        }
    }

    fun add(key: String, value: String) {
        params.add(Parameter(key, value))
    }

    fun appendTo(url: String): String {
        val queryString = asFormUrlEncodedString()
        return if (queryString == EMPTY_STRING) {
            url
        } else {
            var resultUrl = url + if (url.indexOf(QUERY_STRING_SEPARATOR) != -1) PARAM_SEPARATOR else QUERY_STRING_SEPARATOR
            resultUrl += queryString
            resultUrl
        }
    }

    fun asOauthBaseString(): String? {
        return OAuthEncoder.encode(asFormUrlEncodedString())
    }

    fun asFormUrlEncodedString(): String {
        if (params.size == 0) return EMPTY_STRING
        val builder = StringBuilder()
        for (p in params) {
            builder.append('&').append(p.asUrlEncodedPair())
        }
        return builder.toString().substring(1)
    }

    fun addAll(other: ParameterList) {
        params.addAll(other.params)
    }

    fun addQuerystring(queryString: String) {
        if (queryString.length > 0) {
            for (param in queryString.split(PARAM_SEPARATOR).toTypedArray()) {
                val pair = param.split(PAIR_SEPARATOR).toTypedArray()
                val key = OAuthEncoder.decode(pair[0])
                val value = if (pair.size > 1) OAuthEncoder.decode(pair[1]) else EMPTY_STRING

                if (key != null && value != null) {
                    params.add(Parameter(key, value))
                }
            }
        }
    }

    operator fun contains(param: Parameter?): Boolean {
        return params.contains(param)
    }

    fun size(): Int {
        return params.size
    }

    fun sort(): ParameterList {
        val sorted = ParameterList(params)
        Collections.sort(sorted.params)
        return sorted
    }

    companion object {
        private const val QUERY_STRING_SEPARATOR = '?'
        private const val PARAM_SEPARATOR = "&"
        private const val PAIR_SEPARATOR = "="
        private const val EMPTY_STRING = ""
    }
}