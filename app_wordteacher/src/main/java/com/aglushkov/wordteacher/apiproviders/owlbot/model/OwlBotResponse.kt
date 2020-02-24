package com.aglushkov.wordteacher.apiproviders.owlbot.model


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.aglushkov.wordteacher.model.WordTeacherWord
import com.aglushkov.wordteacher.model.WordTeacherWordDefinition
import com.aglushkov.wordteacher.model.fromString
import java.util.*

@Parcelize
data class OwlBotResponse(
    @SerializedName("definitions") val definitions: List<OwlBotDefinition>,
    @SerializedName("pronunciation") val pronunciation: String?,
    @SerializedName("word") val word: String?
) : Parcelable

fun OwlBotResponse.asWordTeacherWord(): WordTeacherWord? {
    if (word == null) return null

    val map: MutableMap<WordTeacherWord.PartOfSpeech, List<WordTeacherWordDefinition>> = EnumMap(WordTeacherWord.PartOfSpeech::class.java)
    for (definition in definitions) {
        val partOfSpeech = WordTeacherWord.PartOfSpeech.fromString(definition.type)
        definition.asWordTeacherWordDefinition()?.let {
            map[partOfSpeech] = listOf(it)
        }
    }

    return WordTeacherWord(word,
            pronunciation,
            map,
            listOf(this@asWordTeacherWord))
}