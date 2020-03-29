package com.aglushkov.wordteacher.apiproviders.wordnik.model


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class WordnikLabel(
    @SerializedName("text") val text: String?,
    @SerializedName("type") val type: String?
) : Parcelable