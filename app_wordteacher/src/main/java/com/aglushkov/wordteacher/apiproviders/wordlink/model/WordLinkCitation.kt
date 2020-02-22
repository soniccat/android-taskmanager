package com.aglushkov.wordteacher.apiproviders.wordlink.model


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class WordLinkCitation(
    @SerializedName("cite") val cite: String?,
    @SerializedName("source") val source: String?
) : Parcelable