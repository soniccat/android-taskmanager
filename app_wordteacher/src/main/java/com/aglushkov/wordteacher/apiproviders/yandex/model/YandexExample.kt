package com.aglushkov.wordteacher.apiproviders.yandex.model


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class YandexExample(
//    @SerializedName("tr") val definitions: List<YandexDefinition>?,

    // Universal attributes
    @SerializedName("text") val text: String,
    @SerializedName("num") val num: String?,
    @SerializedName("pos") val pos: String?,
    @SerializedName("gen") val gender: String?,
    @SerializedName("asp") val asp: String?
) : Parcelable