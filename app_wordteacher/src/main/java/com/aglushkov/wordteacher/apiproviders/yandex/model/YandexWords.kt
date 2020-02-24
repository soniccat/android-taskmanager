package com.aglushkov.wordteacher.apiproviders.yandex.model


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class YandexWords(
    @SerializedName("def") val words: List<YandexWord>
) : Parcelable