package com.aglushkov.wordteacher.repository

import com.aglushkov.wordteacher.service.ConfigService
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.StandardCharsets
import java.util.*


data class ConfigConnectParamsStat (
        @SerializedName("type") val type: Config.Type,
        @SerializedName("connectParamsHash") val connectParamsHash: Int,
        @SerializedName("errorDate") var errorDate: Date,
        @SerializedName("nextTryDate") var nextTryDate: Date
) {
    companion object
}

fun ConfigConnectParamsStat.Companion.loadFromStream(stream: InputStream): List<ConfigConnectParamsStat> {
    val text = stream.bufferedReader(Charsets.UTF_8).readText()
    return decode(text)
}

fun List<ConfigConnectParamsStat>.saveToStream(stream: OutputStream) {
    val text = ConfigConnectParamsStat.encode(this)
    stream.bufferedWriter(Charsets.UTF_8).write(text)
}

fun ConfigConnectParamsStat.Companion.decode(text: String): List<ConfigConnectParamsStat> {
    val gson = GsonBuilder().create()
    val type = object : TypeToken<List<ConfigConnectParamsStat>>() {}.type
    return gson.fromJson(text, type)
}

fun ConfigConnectParamsStat.Companion.encode(stats: List<ConfigConnectParamsStat>): String {
    val gson = GsonBuilder().create()
    return gson.toJson(stats)
}