package com.aglushkov.wordteacher.apiproviders.wordlink.service

import android.content.Context
import com.aglushkov.wordteacher.R
import com.aglushkov.wordteacher.apiproviders.wordlink.model.WordLinkDefinition
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WordLinkService {
    companion object

    @GET("v4/word.json/{word}/definitions")
    suspend fun definitions(@Path("word") word: String,
                            @Query("sourceDictionaries") dictionaries: String,
                            @Query("limit") limit: Int,
                            @Query("partOfSpeech") partOfSpeech: String?,
                            @Query("includeRelated") includeRelated: Boolean,
                            @Query("useCanonical") useCanonical: Boolean,
                            @Query("includeTags") includeTags: Boolean): List<WordLinkDefinition>
}

fun WordLinkService.Companion.create(context: Context): WordLinkService =
        createRetrofit(context).create(WordLinkService::class.java)

fun WordLinkService.Companion.createRetrofit(context: Context): Retrofit {
    val key = context.getString(R.string.wordlink_key)
    val client = OkHttpClient.Builder().addInterceptor { chain ->
        val request = chain.request()
        val newHttpUrl = request.url().newBuilder()
                .addQueryParameter("api_key", key).build()
        val newRequest: Request = request.newBuilder()
                .url(newHttpUrl).build()
        chain.proceed(newRequest)
    }.build()

    return Retrofit.Builder()
            .baseUrl("https://api.wordnik.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}