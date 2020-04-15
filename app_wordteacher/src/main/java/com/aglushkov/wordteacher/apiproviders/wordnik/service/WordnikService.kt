package com.aglushkov.wordteacher.apiproviders.wordnik.service

import com.aglushkov.wordteacher.apiproviders.wordnik.model.WordnikWord
import com.aglushkov.wordteacher.apiproviders.wordnik.model.asWordTeacherWords
import com.aglushkov.wordteacher.model.WordTeacherWord
import com.aglushkov.wordteacher.repository.Config
import com.aglushkov.wordteacher.repository.ServiceMethodParams
import com.aglushkov.wordteacher.service.WordTeacherWordService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WordnikService {
    enum class Dictionary(val value: String) {
        All("all"),
        Ahd5("ahd-5"),
        AhdLegacy("ahd-legacy"),
        Century("century"),
        Wikitionary("wiktionary"),
        Webster("webster"),
        Wordnet("wordnet");
    }

    enum class PartOfSpeech(value: String) {
        Noun("noun"),
        Adjective("adjective"),
        Verb("verb"),
        Adverb("adverb"),
        Interjection("interjection"),
        Pronoun("pronoun"),
        Preposition("preposition"),
        Abbreviation("abbreviation"),
        Affix("affix"),
        Article("article"),
        AuxiliaryVerb("auxiliary-verb "),
        Conjunction("conjunction"),
        DefiniteArticle("definite-article"),
        FamilyName("family-name"),
        GivenName("given-name"),
        Idiom("idiom"),
        Imperative("imperative"),
        NounPlural("noun-plural"),
        NounPossessive("noun-posessive"),
        PastParticiple("past-participle"),
        PhrasalPrefix("phrasal-prefix"),
        ProperNoun("proper-noun"),
        ProperNounPlural("proper-noun-plural"),
        ProperNounPossessive("proper-noun-posessive"),
        Suffix("suffix"),
        VerbIntransitive("verb-intransitive"),
        VerbTransitive("verb-transitive"),
    }

    companion object {
        val Definitions = "wordnik_definitions"
        val DefinitionsSourceDictionaries = "wordnik_definitions_sourceDictionaries"
        val DefinitionsLimit = "wordnik_definitions_limit"
        val DefinitionsPartOfSpeech = "wordnik_definitions_partOfSpeech"
        val DefinitionsIncludeRelated = "wordnik_definitions_includeRelated"
        val DefinitionsUseCanonical = "wordnik_definitions_useCanonical"
        val DefinitionsIncludeTags = "wordnik_definitions_includeTags"
    }

    @GET("v4/word.json/{word}/definitions")
    suspend fun definitions(@Path("word") word: String,
                            @Query("sourceDictionaries") dictionaries: String,
                            @Query("limit") limit: Int,
                            @Query("partOfSpeech") partOfSpeech: String?,
                            @Query("includeRelated") includeRelated: Boolean,
                            @Query("useCanonical") useCanonical: Boolean,
                            @Query("includeTags") includeTags: Boolean): List<WordnikWord>
}

fun WordnikService.Companion.createRetrofit(baseUrl: String, authInterceptor: Interceptor): Retrofit {
    val client = OkHttpClient.Builder().addInterceptor(authInterceptor).build()
    return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}

fun WordnikService.Companion.create(baseUrl: String, authInterceptor: Interceptor): WordnikService =
        createRetrofit(baseUrl, authInterceptor).create(WordnikService::class.java)

fun WordnikService.Companion.createWordTeacherWordService(aBaseUrl: String,
                                                          aKey: String,
                                                          params: ServiceMethodParams): WordTeacherWordService {
    return object : WordTeacherWordService {
        override var type: Config.Type = Config.Type.Wordnik
        override var key = aKey
        override var baseUrl = aBaseUrl
        override var methodParams = params

        private val authInterceptor = Interceptor { chain ->
            val request = chain.request()
            val newHttpUrl = request.url().newBuilder()
                    .addQueryParameter("api_key", key).build()
            val newRequest: Request = request.newBuilder()
                    .url(newHttpUrl).build()
            chain.proceed(newRequest)
        }

        private val service = WordnikService.create(aBaseUrl, authInterceptor)

        override suspend fun define(word: String): List<WordTeacherWord> {
            val definitions = methodParams.value[Definitions]
            val dictionaries = definitions?.get(DefinitionsSourceDictionaries) ?: WordnikService.Dictionary.Wikitionary.value
            val limit = definitions?.get(DefinitionsLimit)?.toIntOrNull() ?: 20
            val partOfSpeech = definitions?.get(DefinitionsPartOfSpeech)
            val includeRelated = definitions?.get(DefinitionsIncludeRelated)?.toBoolean() ?: false
            val useCanonical = definitions?.get(DefinitionsUseCanonical)?.toBoolean() ?: false
            val includeTags = definitions?.get(DefinitionsIncludeTags)?.toBoolean() ?: false
            return service.definitions(word,
                    dictionaries,
                    limit,
                    partOfSpeech,
                    includeRelated,
                    useCanonical,
                    includeTags).asWordTeacherWords()
        }
    }
}