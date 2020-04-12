package com.aglushkov.wordteacher.apiproviders.google.service

import com.aglushkov.wordteacher.apiproviders.google.model.GoogleWord
import com.aglushkov.wordteacher.apiproviders.google.model.asWordTeacherWord
import com.aglushkov.wordteacher.apiproviders.owlbot.model.asWordTeacherWord
import com.aglushkov.wordteacher.apiproviders.owlbot.service.create
import com.aglushkov.wordteacher.model.WordTeacherWord
import com.aglushkov.wordteacher.repository.ServiceMethodParams
import com.aglushkov.wordteacher.service.WordTeacherWordService
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface GoogleService {
    companion object {
        val Entries = "google_entries"
        val EntriesLang = "google_entries_lang"
    }

    @GET("api/v1/entries/{lang}/{word}")
    suspend fun definitions(@Path("word") word: String,
                            @Path("lang") lang: String): List<GoogleWord>
}

fun GoogleService.Companion.createRetrofit(baseUrl: String): Retrofit {
    return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}

fun GoogleService.Companion.create(baseUrl: String): GoogleService =
        createRetrofit(baseUrl).create(GoogleService::class.java)

fun GoogleService.Companion.createWordTeacherWordService(aBaseUrl: String,
                                                         params: ServiceMethodParams): WordTeacherWordService {
    return object : WordTeacherWordService {
        override var name = "Google"
        override var key = ""
        override var baseUrl = aBaseUrl
        override var methodParams = params

        private val service = GoogleService.create(aBaseUrl)

        override suspend fun define(word: String): List<WordTeacherWord> {
            val lang: String = methodParams.value[Entries]?.get(EntriesLang) ?: "en"
            return service.definitions(word, lang).mapNotNull { it.asWordTeacherWord() }
        }
    }
}