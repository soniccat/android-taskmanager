package com.aglushkov.wordteacher.apiproviders.owlbot.service

import com.aglushkov.wordteacher.apiproviders.owlbot.model.OwlBotWord
import com.aglushkov.wordteacher.apiproviders.owlbot.model.asWordTeacherWord
import com.aglushkov.wordteacher.model.WordTeacherWord
import com.aglushkov.wordteacher.repository.Config
import com.aglushkov.wordteacher.repository.ConfigConnectParams
import com.aglushkov.wordteacher.repository.ServiceMethodParams
import com.aglushkov.wordteacher.service.WordTeacherWordService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface OwlBotService {
    companion object

    @GET("api/v4/dictionary/{word}")
    suspend fun definition(@Path("word") word: String): OwlBotWord
}

fun OwlBotService.Companion.createRetrofit(baseUrl: String, authInterceptor: Interceptor): Retrofit {
    val client = OkHttpClient.Builder().addInterceptor(authInterceptor).build()
    return Retrofit.Builder()
            .client(client)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}

fun OwlBotService.Companion.create(baseUrl: String, authInterceptor: Interceptor): OwlBotService =
        createRetrofit(baseUrl, authInterceptor).create(OwlBotService::class.java)

fun OwlBotService.Companion.createWordTeacherWordService(aBaseUrl: String,
                                                         aKey: String): WordTeacherWordService {
    return object : WordTeacherWordService {
        override var name = "OwlBot"
        override var key = aKey
        override var baseUrl = aBaseUrl
        override var methodParams = ServiceMethodParams(emptyMap())

        private val authInterceptor = Interceptor { chain ->
            val newRequest: Request = chain.request().newBuilder()
                    .addHeader("Authorization", "Token $key")
                    .build()
            chain.proceed(newRequest)
        }

        private val service = OwlBotService.create(aBaseUrl, authInterceptor)

        override suspend fun define(word: String): List<WordTeacherWord> {
            return service.definition(word).asWordTeacherWord()?.let {
                listOf(it)
            } ?: emptyList()
        }
    }
}
