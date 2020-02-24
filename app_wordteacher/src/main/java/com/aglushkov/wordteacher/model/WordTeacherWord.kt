package com.aglushkov.wordteacher.model

import android.util.Log
import java.util.*

interface WordTeacherWord {
    enum class PartOfSpeech {
        Noun,
        Verb,
        Adjective,
        Adverb,
        Pronoun,
        Preposition,
        Conjunction,
        Interjection,
        Abbreviation,
        Undefined;

        companion object
    }

    companion object

    val word: String
    val transcription: String?
    val definitions: Map<PartOfSpeech, List<WordTeacherWordDefinition>>

    val originalSources: List<Any>
}

fun WordTeacherWord.PartOfSpeech.Companion.fromString(string: String?): WordTeacherWord.PartOfSpeech {
    return when(string?.toLowerCase(Locale.US)) {
        "noun" -> WordTeacherWord.PartOfSpeech.Noun
        "verb" -> WordTeacherWord.PartOfSpeech.Verb
        "adjective" -> WordTeacherWord.PartOfSpeech.Adjective
        "adverb" -> WordTeacherWord.PartOfSpeech.Adverb
        "pronoun" -> WordTeacherWord.PartOfSpeech.Pronoun
        "preposition" -> WordTeacherWord.PartOfSpeech.Preposition
        "conjunction" -> WordTeacherWord.PartOfSpeech.Conjunction
        "interjection" -> WordTeacherWord.PartOfSpeech.Interjection
        "abbreviation" -> WordTeacherWord.PartOfSpeech.Abbreviation
        else -> {
            if (string != null) {
                Log.d("WordTeacherWord", "New Part of Speech has found: $string")
            }
            WordTeacherWord.PartOfSpeech.Undefined
        }
    }
}