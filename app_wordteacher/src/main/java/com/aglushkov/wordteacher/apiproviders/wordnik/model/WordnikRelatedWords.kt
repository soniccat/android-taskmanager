package com.aglushkov.wordteacher.apiproviders.wordnik.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class WordnikRelatedWords (
    @SerializedName("relationshipType") val relationshipType: String?,
    @SerializedName("gram") val gram: String?,
    @SerializedName("words") val words: List<String>
) : Parcelable