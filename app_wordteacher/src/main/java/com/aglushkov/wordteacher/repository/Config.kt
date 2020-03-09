package com.aglushkov.wordteacher.repository

import com.google.gson.annotations.SerializedName


// Translation service config
// Provides method description with paras for every service
data class Config(
        @SerializedName("type") val type: Type,
        @SerializedName("baseUrls") val baseUrls: List<String>,
        @SerializedName("keys") val keys: List<String>,
        @SerializedName("params") val methodOptions: ServiceMethodParams = ServiceMethodParams(emptyMap())) {

    enum class Type {
        @SerializedName("google") Google,
        @SerializedName("owlbot") OwlBot,
        @SerializedName("wordlink") Wordlink,
        @SerializedName("yandex") Yandex
    }
}

inline class ServiceMethodParams(val value: Map<String, Map<String, String>>)
