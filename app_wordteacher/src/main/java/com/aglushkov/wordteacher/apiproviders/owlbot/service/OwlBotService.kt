package com.aglushkov.wordteacher.apiproviders.owlbot.service

import android.content.Context
import com.aglushkov.wordteacher.R
import com.aglushkov.wordteacher.apiproviders.owlbot.model.OwlBotWord
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface OwlBotService {
    companion object

    @GET("api/v4/dictionary/{word}")
    suspend fun definitions(@Path("word") word: String): OwlBotWord
}

fun OwlBotService.Companion.create(context: Context): OwlBotService =
        createRetrofit(context).create(OwlBotService::class.java)

fun OwlBotService.Companion.createRetrofit(context: Context): Retrofit {
    val token = context.getString(R.string.owlbot_token)
    val client = OkHttpClient.Builder().addInterceptor { chain ->
        val newRequest: Request = chain.request().newBuilder()
                .addHeader("Authorization", "Token $token")
                .build()
        chain.proceed(newRequest)
    }.build()

    return Retrofit.Builder()
            .client(client)
            .baseUrl("https://owlbot.info/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}