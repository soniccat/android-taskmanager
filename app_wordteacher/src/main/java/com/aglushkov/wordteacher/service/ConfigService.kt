package com.aglushkov.wordteacher.service

import com.aglushkov.wordteacher.repository.Config
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.http.GET
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets


interface ConfigService {
    companion object

    @GET("wordteacher/config")
    suspend fun config(): ResponseBody
}

fun ConfigService.Companion.createRetrofit(baseUrl: String): Retrofit {
    val client = OkHttpClient.Builder().build()
    return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .build()
}

fun ConfigService.Companion.create(aBaseUrl: String): ConfigService =
        createRetrofit(aBaseUrl).create(ConfigService::class.java)

fun ConfigService.Companion.decodeConfigs(body: ResponseBody): List<Config> {
    val byteArray = body.bytes()
    return decodeConfigs(byteArray)
}

fun ConfigService.Companion.decodeConfigs(byteArray: ByteArray): List<Config> {
    val gson = GsonBuilder().create()
    val type = object : TypeToken<List<Config>>() {}.type
    return gson.fromJson(byteArray.toString(StandardCharsets.UTF_8), type)
}

fun ConfigService.Companion.encodeConfigs(configs: List<Config>): ByteArray {
    val gson = GsonBuilder().create()
    return gson.toJson(configs).toByteArray()
}