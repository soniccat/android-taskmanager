package com.aglushkov.wordteacher.apiproviders.owlbot.model


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.aglushkov.wordteacher.model.WordTeacherWord
import com.aglushkov.wordteacher.model.WordTeacherDefinition
import com.aglushkov.wordteacher.model.fromString
import java.util.*

@Parcelize
data class OwlBotWord(
    @SerializedName("definitions") val definitions: List<OwlBotDefinition>,
    @SerializedName("pronunciation") val pronunciation: String?,
    @SerializedName("word") val word: String?
) : Parcelable

fun OwlBotWord.asWordTeacherWord(): WordTeacherWord? {
    if (word == null) return null

    val map: MutableMap<WordTeacherWord.PartOfSpeech, List<WordTeacherDefinition>> = EnumMap(WordTeacherWord.PartOfSpeech::class.java)
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