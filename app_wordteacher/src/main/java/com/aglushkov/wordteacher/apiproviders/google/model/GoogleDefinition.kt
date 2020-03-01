package com.aglushkov.wordteacher.apiproviders.google.model


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.aglushkov.wordteacher.model.WordTeacherDefinition

@Parcelize
data class GoogleDefinition(
    @SerializedName("definition") val definition: String,
    @SerializedName("example") val example: String?,
    @SerializedName("synonyms") val synonyms: List<String>?
) : Parcelable

fun GoogleDefinition.asWordTeacherDefinition(): WordTeacherDefinition? {
    return WordTeacherDefinition(definition,
            if (example != null) listOf(example) else emptyList(),
            synonyms.orEmpty(),
            null,
            listOf(this@asWordTeacherDefinition))
}