package com.aglushkov.wordteacher.apiproviders.yandex.model


import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@Parcelize
data class YandexDefinitions(
    @SerializedName("def") val definitions: List<YandexDefinition>
) : Parcelable