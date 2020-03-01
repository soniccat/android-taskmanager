package com.aglushkov.wordteacher.apiproviders.owlbot.service

import android.content.Context
import com.aglushkov.wordteacher.R
import com.aglushkov.wordteacher.apiproviders.owlbot.model.OwlBotWord
import com.aglushkov.wordteacher.apiproviders.owlbot.model.asWordTeacherWord
import com.aglushkov.wordteacher.model.WordTeacherWord
import com.aglushkov.wordteacher.repository.ServiceMethodParams
import com.aglushkov.wordteacher.service.WordTeacherWordService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface OwlBotService {
    companion object

    @GET("api/v4/dictionary/{word}")
    suspend fun definitions(@Path("word") word: String): OwlBotWord
}

fun OwlBotService.Companion.create(authInterceptor: Interceptor): OwlBotService =
        createRetrofit(authInterceptor).create(OwlBotService::class.java)

fun OwlBotService.Companion.createRetrofit(authInterceptor: Interceptor): Retrofit {
    val client = OkHttpClient.Builder().addInterceptor(authInterceptor).build()
    return Retrofit.Builder()
            .client(client)
            .baseUrl("https://owlbot.info/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}

fun OwlBotService.Companion.createWordTeacherWordService(aKey: String): WordTeacherWordService {
    return object : WordTeacherWordService {
        override var key: String = aKey
        override var options = ServiceMethodParams(emptyMap())

        private val authInterceptor = Interceptor { chain ->
            val newRequest: Request = chain.request().newBuilder()
                    .addHeader("Authorization", "Token $key")
                    .build()
            chain.proceed(newRequest)
        }

        private val service = OwlBotService.create(authInterceptor)

        override suspend fun define(word: String): List<WordTeacherWord> {
            return service.definitions(word).asWordTeacherWord()?.let {
                listOf(it)
            } ?: emptyList()
        }
    }
}