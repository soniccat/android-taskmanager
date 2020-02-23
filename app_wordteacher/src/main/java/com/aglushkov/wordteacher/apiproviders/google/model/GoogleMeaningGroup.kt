package com.aglushkov.wordteacher.apiproviders.google.model


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class GoogleMeaningGroup(
    @SerializedName("noun") val nouns: List<GoogleMeaning>?,
    @SerializedName("verb") val verbs: List<GoogleMeaning>?,
    @SerializedName("adjective") val adjectives: List<GoogleMeaning>?,
    @SerializedName("adverb") val adverbs: List<GoogleMeaning>?,
    @SerializedName("pronoun") val pronouns: List<GoogleMeaning>?,
    @SerializedName("determiner") val determiners: List<GoogleMeaning>?,
    @SerializedName("proper noun") val properNouns: List<GoogleMeaning>?,
    @SerializedName("relative pronoun & determiner") val relativePronounAndDeterminers: List<GoogleMeaning>?,
    @SerializedName("nom_masculin") val nomMasculins: List<GoogleMeaning>?,
    @SerializedName("transitive verb") val transitiveVerbs: List<GoogleMeaning>?,
    @SerializedName("intransitive verb") val intransitiveVerbs: List<GoogleMeaning>?,
    @SerializedName("abbreviation") val abbreviations: List<GoogleMeaning>?,
    @SerializedName("exclamation") val exclamations: List<GoogleMeaning>?
) : Parcelable