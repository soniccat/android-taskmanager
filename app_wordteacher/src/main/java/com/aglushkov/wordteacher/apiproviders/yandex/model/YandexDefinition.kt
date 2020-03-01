package com.aglushkov.wordteacher.apiproviders.yandex.model


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.aglushkov.wordteacher.model.WordTeacherDefinition

@Parcelize
data class YandexDefinition(
    @SerializedName("ex") val examples: List<YandexExample>?,
    @SerializedName("mean") val meanings: List<YandexMeaning>?,
    @SerializedName("syn") val synonyms: List<YandexSynonym>?,

    // Universal attributes
    @SerializedName("text") val text: String,
    @SerializedName("num") val num: String?,
    @SerializedName("pos") val pos: String?,
    @SerializedName("gen") val gender: String?,
    @SerializedName("asp") val asp: String?
) : Parcelable

fun YandexDefinition.asWordTeacherDefinition(): WordTeacherDefinition? {
    val resultExamples = examples.orEmpty().map { it.text }
    val resultSynonyms = synonyms.orEmpty().map { it.text }
    // TODO: support meanings for non english definitions

    return WordTeacherDefinition(text,
            resultExamples,
            resultSynonyms,
            null,
            listOf(this@asWordTeacherDefinition))
}