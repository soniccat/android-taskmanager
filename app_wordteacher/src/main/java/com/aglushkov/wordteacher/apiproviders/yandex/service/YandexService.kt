package com.aglushkov.wordteacher.apiproviders.yandex.service

import android.content.Context
import com.aglushkov.wordteacher.R
import com.aglushkov.wordteacher.apiproviders.yandex.model.YandexWords
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface YandexService {
    companion object

    @GET("api/v1/dicservice.json/lookup")
    suspend fun definitions(@Query("text") word: String,
                            @Query("lang") languages: String,
                            @Query("ui") uiLang: String,
                            @Query("flags") flags: Int): YandexWords
}

fun YandexService.Companion.create(context: Context): YandexService =
        createRetrofit(context).create(YandexService::class.java)

fun YandexService.Companion.createRetrofit(context: Context): Retrofit {
    val key = context.getString(R.string.yandex_key)
    val client = OkHttpClient.Builder().addInterceptor { chain ->
        val request = chain.request()
        val newHttpUrl = request.url().newBuilder()
                .addQueryParameter("key", key).build()
        val newRequest: Request = request.newBuilder()
                .url(newHttpUrl).build()
        chain.proceed(newRequest)
    }.build()

    return Retrofit.Builder()
            .baseUrl("https://dictionary.yandex.net/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}