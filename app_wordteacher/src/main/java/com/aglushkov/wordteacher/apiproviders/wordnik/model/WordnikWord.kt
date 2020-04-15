package com.aglushkov.wordteacher.apiproviders.wordnik.model


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.aglushkov.wordteacher.model.WordTeacherDefinition
import com.aglushkov.wordteacher.model.WordTeacherWord
import com.aglushkov.wordteacher.model.fromString

// TODO: check possible values of WordnikLinkRelatedWords.relationshipType
@Parcelize
data class WordnikWord(
        @SerializedName("id") val id: String?,
        @SerializedName("attributionText") val attributionText: String?,
        @SerializedName("attributionUrl") val attributionUrl: String?,
        @SerializedName("citations") val citations: List<WordnikCitation>,
        @SerializedName("exampleUses") val exampleUses: List<WordnikExampleUse>,
        @SerializedName("labels") val labels: List<WordnikLabel>,
//        @SerializedName("notes") val notes: List<Any>,
        @SerializedName("partOfSpeech") val partOfSpeech: String?,
        @SerializedName("relatedWords") val relatedWords: List<WordnikRelatedWords>,
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

fun List<WordnikWord>.asWordTeacherWords(): List<WordTeacherWord> {
    val map: MutableMap<String, WordTeacherWord> = mutableMapOf()

    for (word in this) {
        if (word.word == null) continue

        val partOfSpeech = WordTeacherWord.PartOfSpeech.fromString(word.partOfSpeech)
        val definition = word.asDefinition() ?: continue
        val wordTeacherWord = map[word.word] ?: run {
            val word = WordTeacherWord(word.word,
                    null,
                    mutableMapOf(partOfSpeech to mutableListOf()),
                    mutableListOf())
            map[word.word] = word
            word
        }

        (wordTeacherWord.originalSources as MutableList).add(word)

        val definitionsMap = wordTeacherWord.definitions as MutableMap
        val definitionsList = definitionsMap[partOfSpeech] as MutableList
        definitionsList.add(definition)
    }

    return map.values.toList()
}

fun WordnikWord.asDefinition(): WordTeacherDefinition? {
    if (text == null) return null

    return WordTeacherDefinition(
            listOf(text),
            exampleUsesTexts(),
            synonyms(),
            null,
            listOf(this)
    )
}