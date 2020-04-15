package com.aglushkov.wordteacher.apiproviders.yandex.model


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.aglushkov.wordteacher.model.WordTeacherDefinition
import com.aglushkov.wordteacher.model.WordTeacherWord
import com.aglushkov.wordteacher.model.fromString
import com.aglushkov.wordteacher.repository.Config
import java.util.*

@Parcelize
data class YandexWord(
    @SerializedName("tr") val definitions: List<YandexDefinition>,
    @SerializedName("ts") val transcription: String?,

    // Universal attributes
    @SerializedName("text") val text: String,
    @SerializedName("num") val num: String?,
    @SerializedName("pos") val pos: String?,
    @SerializedName("gen") val gender: String?,
    @SerializedName("asp") val asp: String?
) : Parcelable

fun YandexWord.asWordTeacherWord(): WordTeacherWord? {
    val map: MutableMap<WordTeacherWord.PartOfSpeech, List<WordTeacherDefinition>> = EnumMap(WordTeacherWord.PartOfSpeech::class.java)
    for (definition in definitions) {
        val partOfSpeech = WordTeacherWord.PartOfSpeech.fromString(definition.pos)
        definition.asWordTeacherDefinition()?.let {
            var list = map[partOfSpeech] as? MutableList<WordTeacherDefinition>
            if (list == null) {
                list = mutableListOf()
                map[partOfSpeech] = list
            }

            list.add(it)
        }
    }

    return WordTeacherWord(text,
            transcription,
            map,
            listOf(Config.Type.Yandex))
}