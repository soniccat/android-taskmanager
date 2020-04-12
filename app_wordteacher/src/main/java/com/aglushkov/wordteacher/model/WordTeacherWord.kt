package com.aglushkov.wordteacher.model

import android.content.Context
import android.os.Parcelable
import android.util.Log
import com.aglushkov.wordteacher.R
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class WordTeacherWord(val word: String,
                           val transcription: String?,
                           val definitions: Map<PartOfSpeech, List<WordTeacherDefinition>>,

                           val originalSources: List<Parcelable>): Parcelable {
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
        Exclamation,
        Determiner,
        Undefined;

        companion object
    }

    companion object
}

fun WordTeacherWord.PartOfSpeech.Companion.fromString(string: String?): WordTeacherWord.PartOfSpeech {
    val resultString = string?.toLowerCase(Locale.US) ?: "null"
    return when {
        resultString.contains("noun") -> WordTeacherWord.PartOfSpeech.Noun
        resultString.contains("verb") -> WordTeacherWord.PartOfSpeech.Verb
        resultString.contains("adverb") -> WordTeacherWord.PartOfSpeech.Adverb
        resultString.contains("adjective") -> WordTeacherWord.PartOfSpeech.Adjective
        resultString == "pronoun" -> WordTeacherWord.PartOfSpeech.Pronoun
        resultString == "preposition" -> WordTeacherWord.PartOfSpeech.Preposition
        resultString == "conjunction" -> WordTeacherWord.PartOfSpeech.Conjunction
        resultString == "interjection" -> WordTeacherWord.PartOfSpeech.Interjection
        resultString == "abbreviation" -> WordTeacherWord.PartOfSpeech.Abbreviation
        resultString == "determiner" -> WordTeacherWord.PartOfSpeech.Determiner
        resultString == "exclamation" -> WordTeacherWord.PartOfSpeech.Exclamation
        else -> {
            if (string != null) {
                Log.d("WordTeacherWord", "New Part of Speech has found: $string")
            }
            WordTeacherWord.PartOfSpeech.Undefined
        }
    }
}

fun WordTeacherWord.PartOfSpeech.toString(context: Context): String {
    return when(this) {
        WordTeacherWord.PartOfSpeech.Noun -> context.getString(R.string.word_partofspeech_noun)
        WordTeacherWord.PartOfSpeech.Verb -> context.getString(R.string.word_partofspeech_verb)
        else -> context.getString(R.string.word_partofspeech_unknown)
    }
}