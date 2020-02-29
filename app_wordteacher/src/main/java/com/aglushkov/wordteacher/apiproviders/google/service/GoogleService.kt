package com.aglushkov.wordteacher.apiproviders.google.service

import com.aglushkov.wordteacher.apiproviders.google.model.GoogleWord
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface GoogleService {
    companion object

    @GET("api/v1/entries/{lang}/{word}")
    suspend fun definitions(@Path("word") word: String,
                            @Path("lang") lang: String): List<GoogleWord>
}

fun GoogleService.Companion.create(): GoogleService =
        createRetrofit().create(GoogleService::class.java)

fun GoogleService.Companion.createRetrofit(): Retrofit {
    return Retrofit.Builder()
            .baseUrl("https://api.dictionaryapi.dev/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}