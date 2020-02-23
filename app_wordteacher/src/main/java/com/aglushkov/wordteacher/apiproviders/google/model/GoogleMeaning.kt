package com.aglushkov.wordteacher.apiproviders.google.model


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class GoogleMeaning(
    @SerializedName("definition") val definition: String,
    @SerializedName("example") val example: String?,
    @SerializedName("synonyms") val synonyms: List<String>?
) : Parcelable