package com.aglushkov.wordteacher.apiproviders.google.model


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class GoogleDefinition(
    @SerializedName("meaning") val meaningGroup: GoogleMeaningGroup,
    @SerializedName("origin") val origin: String?,
    @SerializedName("phonetic") val phonetic: String?,
    @SerializedName("word") val word: String
) : Parcelable