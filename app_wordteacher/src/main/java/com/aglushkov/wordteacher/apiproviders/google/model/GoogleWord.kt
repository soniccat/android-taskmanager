package com.aglushkov.wordteacher.apiproviders.google.model


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.aglushkov.wordteacher.model.WordTeacherWord
import com.aglushkov.wordteacher.repository.Config
import java.util.*

@Parcelize
data class GoogleWord(
    @SerializedName("meaning") val definitions: GoogleDefinitions,
    @SerializedName("origin") val origin: String?,
    @SerializedName("phonetic") val phonetic: String?,
    @SerializedName("word") val word: String
) : Parcelable

fun GoogleWord.asWordTeacherWord(): WordTeacherWord? {
    return WordTeacherWord(word,
            phonetic,
            definitions.asMap(),
            listOf(Config.Type.Google))
}