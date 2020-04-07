package com.aglushkov.wordteacher.apiproviders.google.model


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import com.aglushkov.wordteacher.general.extensions.merge
import com.aglushkov.wordteacher.general.extensions.runIfNotEmpty
import com.aglushkov.wordteacher.model.WordTeacherDefinition
import com.aglushkov.wordteacher.model.WordTeacherWord

// All possible types were found on https://github.com/meetDeveloper/googleDictionaryAPI/issues/32
// TODO: add types not added here
@Parcelize
data class GoogleDefinitions(
    @SerializedName("noun") val nouns: List<GoogleDefinition>?,
    @SerializedName("proper noun") val properNouns: List<GoogleDefinition>?,
    @SerializedName("plural noun") val pluralNouns: List<GoogleDefinition>?,

    @SerializedName("verb") val verbs: List<GoogleDefinition>?,
    @SerializedName("transitive verb") val transitiveVerbs: List<GoogleDefinition>?,
    @SerializedName("intransitive verb") val intransitiveVerbs: List<GoogleDefinition>?,
    @SerializedName("modal verb") val modalVerbs: List<GoogleDefinition>?,
    @SerializedName("auxiliary verb") val auxiliaryVerbs: List<GoogleDefinition>?,

    @SerializedName("adjective") val adjectives: List<GoogleDefinition>?,
    @SerializedName("adjective & determiner") val adjectivesAndDetermines: List<GoogleDefinition>?,
    @SerializedName("adjective & pronoun") val adjectivesAndPronouns: List<GoogleDefinition>?,
    @SerializedName("determiner, pronoun, & adjective") val determinersPronounsAndAdjectives: List<GoogleDefinition>?,

    @SerializedName("adverb") val adverbs: List<GoogleDefinition>?,
    @SerializedName("interrogative adverb") val interrogativeAdverbs: List<GoogleDefinition>?,
    @SerializedName("preposition & adverb") val prepositionsAndAdverbs: List<GoogleDefinition>?,
    @SerializedName("adverb & adjective") val adverbsAndAdjectives: List<GoogleDefinition>?,
    @SerializedName("conjunction & adverb") val conjunctionAndAdverb: List<GoogleDefinition>?,
    @SerializedName("preposition, conjunction, & adverb") val prepositionsConjunctionsAndAdverb: List<GoogleDefinition>?,

    @SerializedName("pronoun") val pronouns: List<GoogleDefinition>?,
    @SerializedName("relative pronoun & determiner") val relativePronounsAndDeterminers: List<GoogleDefinition>?,

    @SerializedName("determiner") val determiners: List<GoogleDefinition>?,
    @SerializedName("abbreviation") val abbreviations: List<GoogleDefinition>?,
    @SerializedName("exclamation") val exclamations: List<GoogleDefinition>?

//    @SerializedName("nom_masculin") val nomMasculins: List<GoogleDefinition>?
) : Parcelable {
    fun allNouns() = nouns.merge(properNouns).merge(pluralNouns)
    fun allVerbs() = verbs.merge(transitiveVerbs).merge(intransitiveVerbs).merge(modalVerbs).merge(auxiliaryVerbs)
    fun allAdjectives() = adjectives.merge(adjectivesAndDetermines).merge(adjectivesAndPronouns)
            .merge(determinersPronounsAndAdjectives)
    fun allAdverbs() = adverbs.merge(interrogativeAdverbs).merge(prepositionsAndAdverbs)
            .merge(adverbsAndAdjectives).merge(adverbsAndAdjectives).merge(conjunctionAndAdverb)
            .merge(prepositionsConjunctionsAndAdverb)
    fun allPronouns() = pronouns.merge(relativePronounsAndDeterminers)
    fun allDetermines() = determiners
}

fun GoogleDefinitions.asMap(): Map<WordTeacherWord.PartOfSpeech, List<WordTeacherDefinition>> {

    fun setDefinitionsToMap(map: MutableMap<WordTeacherWord.PartOfSpeech, List<WordTeacherDefinition>>,
                            partOfSpeech: WordTeacherWord.PartOfSpeech,
                            googleDefinitions: List<GoogleDefinition>?) {
        googleDefinitions?.runIfNotEmpty {
            map[partOfSpeech] = googleDefinitions.map { d -> d.asWordTeacherDefinition()!! }
        }
    }

    val map = mutableMapOf<WordTeacherWord.PartOfSpeech, List<WordTeacherDefinition>>()
    setDefinitionsToMap(map, WordTeacherWord.PartOfSpeech.Noun, allNouns())
    setDefinitionsToMap(map, WordTeacherWord.PartOfSpeech.Verb, allVerbs())
    setDefinitionsToMap(map, WordTeacherWord.PartOfSpeech.Adjective, allAdjectives())
    setDefinitionsToMap(map, WordTeacherWord.PartOfSpeech.Adverb, allAdverbs())
    setDefinitionsToMap(map, WordTeacherWord.PartOfSpeech.Pronoun, allPronouns())
    setDefinitionsToMap(map, WordTeacherWord.PartOfSpeech.Determiner, allDetermines())
    setDefinitionsToMap(map, WordTeacherWord.PartOfSpeech.Abbreviation, abbreviations)
    setDefinitionsToMap(map, WordTeacherWord.PartOfSpeech.Exclamation, exclamations)

    return map
}