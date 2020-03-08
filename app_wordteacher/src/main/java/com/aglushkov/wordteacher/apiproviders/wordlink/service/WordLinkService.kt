package com.aglushkov.wordteacher.apiproviders.wordlink.service

import com.aglushkov.wordteacher.apiproviders.wordlink.model.WordLinkWord
import com.aglushkov.wordteacher.apiproviders.wordlink.model.asWordTeacherWords
import com.aglushkov.wordteacher.model.WordTeacherWord
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

interface WordLinkService {
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
        val Definitions = "wordlink_definitions"
        val DefinitionsSourceDictionaries = "wordlink_definitions_sourceDictionaries"
        val DefinitionsLimit = "wordlink_definitions_limit"
        val DefinitionsPartOfSpeech = "wordlink_definitions_partOfSpeech"
        val DefinitionsIncludeRelated = "wordlink_definitions_includeRelated"
        val DefinitionsUseCanonical = "wordlink_definitions_useCanonical"
        val DefinitionsIncludeTags = "wordlink_definitions_includeTags"
    }

    @GET("v4/word.json/{word}/definitions")
    suspend fun definitions(@Path("word") word: String,
                            @Query("sourceDictionaries") dictionaries: String,
                            @Query("limit") limit: Int,
                            @Query("partOfSpeech") partOfSpeech: String?,
                            @Query("includeRelated") includeRelated: Boolean,
                            @Query("useCanonical") useCanonical: Boolean,
                            @Query("includeTags") includeTags: Boolean): List<WordLinkWord>
}

fun WordLinkService.Companion.createRetrofit(baseUrl: String, authInterceptor: Interceptor): Retrofit {
    val client = OkHttpClient.Builder().addInterceptor(authInterceptor).build()
    return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}

fun WordLinkService.Companion.create(baseUrl: String, authInterceptor: Interceptor): WordLinkService =
        createRetrofit(baseUrl, authInterceptor).create(WordLinkService::class.java)

fun WordLinkService.Companion.createWordTeacherWordService(aBaseUrl: String,
                                                         aKey: String,
                                                         methodParams: ServiceMethodParams): WordTeacherWordService {
    return object : WordTeacherWordService {
        override var key = aKey
        override var baseUrl = aBaseUrl
        override var methodParams = methodParams

        private val authInterceptor = Interceptor { chain ->
            val request = chain.request()
            val newHttpUrl = request.url().newBuilder()
                    .addQueryParameter("api_key", key).build()
            val newRequest: Request = request.newBuilder()
                    .url(newHttpUrl).build()
            chain.proceed(newRequest)
        }

        private val service = WordLinkService.create(aBaseUrl, authInterceptor)

        override suspend fun define(word: String): List<WordTeacherWord> {
            val definitions = methodParams.value[Definitions]
            val dictionaries = definitions?.get(DefinitionsSourceDictionaries) ?: WordLinkService.Dictionary.Wikitionary.value
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