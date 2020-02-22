package com.aglushkov.wordteacher.apiproviders.wordlink.model


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class WordLinkExampleUse(
    @SerializedName("position") val position: Int,
    @SerializedName("text") val text: String?
) : Parcelable