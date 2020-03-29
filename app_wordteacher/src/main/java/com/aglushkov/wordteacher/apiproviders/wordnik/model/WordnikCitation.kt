package com.aglushkov.wordteacher.apiproviders.wordnik.model


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class WordnikCitation(
    @SerializedName("cite") val cite: String?,
    @SerializedName("source") val source: String?
) : Parcelable