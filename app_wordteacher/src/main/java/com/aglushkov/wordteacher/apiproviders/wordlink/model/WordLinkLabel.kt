package com.aglushkov.wordteacher.apiproviders.wordlink.model


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class WordLinkLabel(
    @SerializedName("text") val text: String?,
    @SerializedName("type") val type: String?
) : Parcelable