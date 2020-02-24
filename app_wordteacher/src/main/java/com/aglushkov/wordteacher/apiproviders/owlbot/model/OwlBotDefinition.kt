package com.aglushkov.wordteacher.apiproviders.owlbot.model


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.aglushkov.wordteacher.model.WordTeacherWordDefinition

@Parcelize
data class OwlBotDefinition(
    @SerializedName("definition") val definition: String?,
//    @SerializedName("emoji") val emoji: String?,
    @SerializedName("example") val example: String?,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("type") val type: String?
) : Parcelable

fun OwlBotDefinition.asWordTeacherWordDefinition(): WordTeacherWordDefinition? {
    if (definition == null) return null

    return object : WordTeacherWordDefinition {
        override val definition: String = this@asWordTeacherWordDefinition.definition
        override val imageUrl = this@asWordTeacherWordDefinition.imageUrl
        override val examples = example?.let {
                listOf(it)
            } ?: run {
                emptyList<String>()
            }

        override val originalSources = listOf(this@asWordTeacherWordDefinition)
    }
}