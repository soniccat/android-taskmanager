package com.aglushkov.wordteacher.apiproviders.wordlink.model


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.aglushkov.wordteacher.model.WordTeacherDefinition
import com.aglushkov.wordteacher.model.WordTeacherWord
import com.aglushkov.wordteacher.model.fromString

// TODO: check possible values of WordLinkRelatedWords.relationshipType
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
    fun exampleUsesTexts() = exampleUses.mapNotNull { it.text } + (citations.mapNotNull { it.cite })
    fun synonyms() = relatedWords.filter { it.relationshipType == "synonym" }.map { it.words }.flatten()
    fun related() = relatedWords.filter { it.relationshipType != "synonym" }.map { it.words }.flatten()
}

fun List<WordLinkWord>.asWordTeacherWords(): List<WordTeacherWord> {
    val map: MutableMap<String, WordTeacherWord> = mutableMapOf()

    for (word in this) {
        if (word.word == null) continue

        val partOfSpeech = WordTeacherWord.PartOfSpeech.fromString(word.partOfSpeech)
        val definition = word.asDefinition() ?: continue
        val wordTeacherWord = map[word.word] ?: WordTeacherWord(word.word,
                null,
                mutableMapOf(partOfSpeech to mutableListOf()),
                mutableListOf())

        (wordTeacherWord.originalSources as MutableList).add(word)

        val definitionsMap = wordTeacherWord.definitions as MutableMap
        val definitionsList = definitionsMap[partOfSpeech] as MutableList
        definitionsList.add(definition)
    }

    return map.values.toList()
}

fun WordLinkWord.asDefinition(): WordTeacherDefinition? {
    if (text == null) return null

    return WordTeacherDefinition(
            text,
            exampleUsesTexts(),
            synonyms(),
            null,
            listOf(this)
    )
}