package com.aglushkov.wordteacher.apiproviders.wordlink.model


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class WordLinkWord(
    @SerializedName("id") val id: String?,
    @SerializedName("attributionText") val attributionText: String?,
    @SerializedName("attributionUrl") val attributionUrl: String?,
    @SerializedName("citations") val citations: List<WordLinkCitation>,
    @SerializedName("exampleUses") val exampleUses: List<WordLinkExampleUse>,
    @SerializedName("labels") val labels: List<WordLinkLabel>,
//        @SerializedName("notes") val notes: List<Any>,
    @SerializedName("partOfSpeech") val partOfSpeech: String?,
    @SerializedName("relatedWords") val relatedWords: List<WordLinkRelatedWords>,
    @SerializedName("sourceDictionary") val sourceDictionary: String?,
    @SerializedName("text") val text: String?,
//        @SerializedName("textProns") val textProns: List<Any>,
    @SerializedName("word") val word: String?,
    @SerializedName("wordnikUrl") val wordnikUrl: String?
) : Parcelable {
    fun exampleUsesTexts() = exampleUses.map { it.text }
}