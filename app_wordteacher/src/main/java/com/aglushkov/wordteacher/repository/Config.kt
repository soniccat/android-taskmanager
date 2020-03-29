package com.aglushkov.wordteacher.repository

import com.google.gson.annotations.SerializedName
import java.util.*


// Translation service config
// Provides method description with paras for every service
data class Config(
    @SerializedName("type") val type: Type,
    @SerializedName("connectParams") val connectParams: List<ConfigConnectParams>,
    @SerializedName("methods") val methods: ServiceMethodParams = ServiceMethodParams(emptyMap())) {

    enum class Type {
        @SerializedName("google") Google,
        @SerializedName("owlbot") OwlBot,
        @SerializedName("wordnik") Wordnik,
        @SerializedName("yandex") Yandex
    }
}

data class ConfigConnectParams(
    @SerializedName("baseUrl") val baseUrl: String,
    @SerializedName("key") val key: String
)

inline class ServiceMethodParams(val value: Map<String, Map<String, String>>)
