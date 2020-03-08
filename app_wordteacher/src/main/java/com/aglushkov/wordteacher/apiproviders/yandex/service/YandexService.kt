package com.aglushkov.wordteacher.apiproviders.yandex.service

import com.aglushkov.wordteacher.apiproviders.yandex.model.YandexWords
import com.aglushkov.wordteacher.apiproviders.yandex.model.asWordTeacherWord
import com.aglushkov.wordteacher.model.WordTeacherWord
import com.aglushkov.wordteacher.repository.ServiceMethodParams
import com.aglushkov.wordteacher.service.WordTeacherWordService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface YandexService {
    companion object {
        val Lookup = "yandex_lookup"
        val LookupLang = "yandex_lookup_lang"
        val LookupUi = "yandex_lookup_ui"
        val LookupFlags = "yandex_lookup_flags"
    }

    @GET("api/v1/dicservice.json/lookup")
    suspend fun definitions(@Query("text") word: String,
                            @Query("lang") languages: String,
                            @Query("ui") uiLang: String,
                            @Query("flags") flags: Int): YandexWords
}

fun YandexService.Companion.createRetrofit(baseUrl: String, authInterceptor: Interceptor): Retrofit {
    val client = OkHttpClient.Builder().addInterceptor(authInterceptor).build()
    return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}

fun YandexService.Companion.create(aBaseUrl: String,
                                   authInterceptor: Interceptor): YandexService =
        createRetrofit(aBaseUrl, authInterceptor).create(YandexService::class.java)

fun YandexService.Companion.createWordTeacherWordService(aBaseUrl: String,
                                                         aKey: String,
                                                         methodParams: ServiceMethodParams): WordTeacherWordService {
    return object : WordTeacherWordService {
        override var key = aKey
        override var baseUrl = aBaseUrl
        override var methodParams = methodParams

        private val authInterceptor = Interceptor { chain ->
            val request = chain.request()
            val newHttpUrl = request.url().newBuilder()
                    .addQueryParameter("key", key).build()
            val newRequest: Request = request.newBuilder()
                    .url(newHttpUrl).build()
            chain.proceed(newRequest)
        }

        private val service = YandexService.create(aBaseUrl, authInterceptor)

        override suspend fun define(word: String): List<WordTeacherWord> {
            val lookup = methodParams.value[Lookup]
            val lang = lookup?.get(LookupLang) ?: "en"
            val ui = lookup?.get(LookupUi) ?: "en"
            val flags = lookup?.get(LookupFlags)?.toIntOrNull() ?: 4
            return service.definitions(word, lang, ui, flags).words.mapNotNull { it.asWordTeacherWord() }
        }
    }
}